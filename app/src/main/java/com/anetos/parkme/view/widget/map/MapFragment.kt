package com.anetos.parkme.view.widget.map

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.anetos.parkme.R
import com.anetos.parkme.core.BaseFragment
import com.anetos.parkme.core.helper.*
import com.anetos.parkme.core.maphelper.MapClusterItem
import com.anetos.parkme.core.maphelper.MarkerClusterRenderer
import com.anetos.parkme.core.maphelper.configureMap
import com.anetos.parkme.data.ConstantFirebase
import com.anetos.parkme.databinding.FragmentMapBinding
import com.anetos.parkme.domain.model.ParkingSpot
import com.anetos.parkme.view.widget.about.AboutDialogFragment
import com.anetos.parkme.view.widget.booking.BookingDialogFragment
import com.anetos.parkme.view.widget.common.ConfirmationDialogFragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.MarkerManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class MapFragment : BaseFragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowLongClickListener {
    private lateinit var binding: FragmentMapBinding
    private val viewModel by viewModels<MapViewModel>()
    private lateinit var mMap: GoogleMap
    private var mLocationRequest: LocationRequest = LocationRequest()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val updateInterval: Long = 10 * 1000
    private val fastestInterval: Long = 2000
    private lateinit var mClusterManager: ClusterManager<MapClusterItem>
    private lateinit var markerCollection: MarkerManager.Collection
    var placeName = ""

    private val anchorViewId by lazy { R.id.fab }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        startLocationUpdate()
        getFirebaseData()

        binding.fab.setOnClickListener {
            mMap.clear()
            mClusterManager.clearItems()
            getFirebaseData()
        }
        binding.bottomAppBar.setOnSwipeGestureListener {

        }

        binding.bottomAppBar.setNavigationOnClickListener {
            activity?.supportFragmentManager?.let {
                AboutDialogFragment().show(it, null)
            }
        }
        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.aboutLegend -> {
                    activity?.supportFragmentManager?.let {
                        LegendDialogFragment().show(it, null)
                    }
                    true
                }
                else -> false
            }
        }

        binding.tb.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    logout()
                }
            }
            false
        }
    }

    fun logout() {
        ConfirmationDialogFragment(
            dialogTitle = "Logout",
            confirmation = "Logout confirmation",
            description = "Are you sure you want to logout from application?",
            buttonText = "Logout",
        ).onClickListener(object : ConfirmationDialogFragment.onConfirmationClickListener {
            override fun onClick(confirmationDialogFragment: ConfirmationDialogFragment) {
                context?.let {
                    SharedPreferenceHelper().clearAppPreferences()
                    FirebaseAuth.getInstance().signOut()
                    binding.root.snackbar(
                        stringId = R.string.logout_successful,
                        drawableId = R.drawable.ic_round_check_circle_24,
                        anchorViewId = anchorViewId,
                        color = AppColor.Success,
                        vibrate = true
                    )
                    confirmationDialogFragment.dismiss()
                }
                withDelay(1000) {
                    activity?.finish()
                    Navigator.toLoginActivity(true)
                }
            }
        }).show(requireActivity().supportFragmentManager, null)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnInfoWindowLongClickListener(this)
        mClusterManager = ClusterManager(requireContext(), mMap)
        context?.configureMap(mMap, false)
    }

    private fun startLocationUpdate() {
        PermissionHelper.checkLocationPermission(requireActivity())

        //Create the location request to start receiving updates
        mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = updateInterval
        mLocationRequest.fastestInterval = fastestInterval

        // Create LocationSettingsRequest object using location request
        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest: LocationSettingsRequest = builder.build()

        val settingsClient: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        settingsClient.checkLocationSettings(locationSettingsRequest)

        LocationServices.getFusedLocationProviderClient(requireActivity()).requestLocationUpdates(
            mLocationRequest,
            locationUpdates,
            null
        )
    }

    private val locationUpdates = object : LocationCallback() {
        override fun onLocationResult(lr: LocationResult) {
            val geoCoder = Geocoder(requireContext(), Locale.getDefault())
            try {
                val address = geoCoder.getFromLocation(
                    lr.locations.last().latitude,
                    lr.locations.last().longitude,
                    1
                )
                placeName = address?.firstOrNull().let { it?.subLocality ?: it?.thoroughfare ?: "" }
                Log.e(TAG, " " + lr.locations.last().latitude + " " + lr.locations.last().longitude)
            } catch (e: IOException) {
                // Handle IOException
            } catch (e: NullPointerException) {
                // Handle NullPointerException
            }

            fusedLocationClient.removeLocationUpdates(this)
            binding.tb.title = placeName
        }
    }

    private fun getFirebaseData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.parkingSpots.collect {
                    if (it.isNotEmpty())
                        mClusterManager.addMarkers(it)
                }
            }
        }
    }

    fun ClusterManager<MapClusterItem>.addMarkers(
        parkingSpotList: List<ParkingSpot>
    ) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            this.renderer = MarkerClusterRenderer(requireContext(), mMap, mClusterManager)
            this@MapFragment.markerCollection = mClusterManager.markerCollection

            mClusterManager.setOnClusterClickListener { item ->
                view?.rootView?.snackbar(
                    string = CLUSTER_CLICK,
                    drawableId = R.drawable.ic_round_error_24,
                    anchorViewId = anchorViewId,
                    vibrate = true
                )
                false
            }

            mMap.setOnCameraIdleListener(this)
            mMap.setOnMarkerClickListener(this)

            for (item in parkingSpotList) {
                val markerOption = MarkerOptions()
                markerOption.position(LatLng(item.latitude, item.longitude))
                markerOption.snippet(Gson().toJson(item))

                //Set Custom InfoWindow Adapter
                this.addItem(markerOption.snippet?.let { MapClusterItem(item, it) })
                this@MapFragment.markerCollection.setInfoWindowAdapter(activity?.let {
                    CustomInfoWindowAdapter(it)
                })
            }
            this@MapFragment.markerCollection.setOnInfoWindowClickListener { marker ->
                showBooking(
                    marker
                )
            }
            this.cluster()
        }
    }

    override fun onInfoWindowLongClick(p0: Marker) {
        view?.rootView?.snackbar(
            string = "Just click to book.",
            drawableId = R.drawable.ic_round_error_24,
            anchorViewId = anchorViewId,
            color = AppColor.Yellow,
            vibrate = true
        )
    }

    class CustomInfoWindowAdapter constructor(var context: Activity) :
        GoogleMap.InfoWindowAdapter {

        @SuppressLint("InflateParams")
        val mContents = context.layoutInflater.inflate(R.layout.view_maps_info_window, null)

        @SuppressLint("InflateParams")
        val mWindow = context.layoutInflater.inflate(R.layout.view_maps_info_window_custom, null)

        @SuppressLint("InflateParams", "NewApi")
        override fun getInfoContents(marker: Marker): View? {
            render(marker, mContents)
            return mContents
        }

        override fun getInfoWindow(marker: Marker): View? {
            render(marker, mWindow)
            return mWindow
        }

        private fun render(marker: Marker, view: View) {
            val place = Gson().fromJson(marker.snippet, ParkingSpot::class.java)
            if (place != null) {
                view.findViewById<TextView>(R.id.locationName).text = place.address
                view.findViewById<TextView>(R.id.price).text =
                    formatAmount(Currency.getInstance(Locale.CANADA), place.pricePerHr).plus("/hr")
                view.findViewById<TextView>(R.id.provider).text = "Parking ID: ${place.parkingId}"
            }
        }
    }

    fun showBooking(marker: Marker) {
        val parkingSpot = Gson().fromJson(marker.snippet, ParkingSpot::class.java)
        if (parkingSpot != null) {
            if (ConstantFirebase.AVAILABILITY_STATUS.AVAILABLE.name.equals(
                    parkingSpot.availabilityStatus,
                    true
                )
            ) {
                BookingDialogFragment(parkingSpot).show(
                    requireActivity().supportFragmentManager,
                    null
                )
            } else {
                view?.rootView?.snackbar(
                    string = ERROR_PARKING_OCCUPIED,
                    drawableId = R.drawable.ic_round_error_24,
                    anchorViewId = anchorViewId,
                    color = AppColor.Error,
                    vibrate = true
                )
            }
        }
    }

    companion object {
        val TAG = MapFragment::class.java.simpleName
        const val ERROR_PARKING_OCCUPIED = "Oops! Parking spot is occupied."
        const val CLUSTER_CLICK = "Double tap to expand."
    }
}