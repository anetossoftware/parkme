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
import com.anetos.parkme.R
import com.anetos.parkme.core.BaseFragment
import com.anetos.parkme.core.helper.*
import com.anetos.parkme.core.maphelper.MapClusterItem
import com.anetos.parkme.core.maphelper.MarkerClusterRenderer
import com.anetos.parkme.core.maphelper.configureMap
import com.anetos.parkme.data.ConstantFirebase
import com.anetos.parkme.data.model.ParkingSpot
import com.anetos.parkme.databinding.FragmentMapBinding
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.maps.android.clustering.ClusterManager
import java.io.IOException
import java.util.*

class MapFragment : BaseFragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
    GoogleMap.OnInfoWindowLongClickListener {
    private lateinit var binding: FragmentMapBinding

    private lateinit var mMap: GoogleMap
    private var mLocationRequest: LocationRequest = LocationRequest()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val updateInterval: Long = 10 * 1000
    private val fastestInterval: Long = 2000
    private lateinit var mClusterManager: ClusterManager<MapClusterItem>
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
                        color = NoteColor.Success,
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
        mMap.setOnInfoWindowClickListener(this)
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
            val geoCoder = Geocoder(context, Locale.getDefault())
            try {
                val address = geoCoder.getFromLocation(
                    lr.locations.last().latitude,
                    lr.locations.last().longitude,
                    1
                )
                placeName = address.firstOrNull().let { it?.subLocality ?: it?.thoroughfare ?: "" }
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

    fun getFirebaseData() {
        // Access a Cloud Firestore instance from your Activity
        val db = Firebase.firestore
        db.collection(ConstantFirebase.COLLECTION_PARKING_SPOT)
            .get()
            .addOnSuccessListener { result ->
                val parkingSpotList: MutableList<ParkingSpot> = ArrayList()
                try {
                    for (document in result) {
                        val data = document.data
                        val parkingSpot = ParkingSpot()
                        parkingSpot.documentId = document.id
                        parkingSpot.parkingId = data.get(ParkingSpot::parkingId.name).toString()
                        parkingSpot.address = data.get(ParkingSpot::address.name).toString()
                        parkingSpot.latitude = data.get(ParkingSpot::latitude.name) as Double
                        parkingSpot.longitude = data.get(ParkingSpot::longitude.name) as Double
                        parkingSpot.pricePerHr = data.get(ParkingSpot::pricePerHr.name) as Double
                        parkingSpot.availabilityStatus =
                            data.get(ParkingSpot::availabilityStatus.name).toString()
                        Log.d(TAG, "${document.id} => ${document.data}")
                        parkingSpotList.add(parkingSpot)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, e.message.toString())
                }

                if (parkingSpotList.size == 0 || parkingSpotList.isEmpty()) {
                    parkingSpotList.add(ParkingSpot()) // for empty-list placeholder
                }
                mClusterManager.addMarkers(parkingSpotList)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }
    }

    fun ClusterManager<MapClusterItem>.addMarkers(
        parkingSpotList: List<ParkingSpot>
    ) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            this.renderer = MarkerClusterRenderer(requireContext(), mMap, mClusterManager)
            mMap.setOnCameraIdleListener(this)
            mMap.setOnMarkerClickListener(this)
            for (item in parkingSpotList) {
                val markerOption = MarkerOptions()
                markerOption.position(LatLng(item.latitude, item.longitude))
                markerOption.snippet(Gson().toJson(item))

                //Set Custom InfoWindow Adapter
                this.addItem(markerOption.snippet?.let { MapClusterItem(item, it) })
                mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(requireActivity()))
            }
            this.cluster()
        }
    }

    class CustomInfoWindowAdapter constructor(var context: Activity) :
        GoogleMap.InfoWindowAdapter {

        @SuppressLint("InflateParams")
        val mContents = context.layoutInflater.inflate(
            R.layout.view_maps_info_window,
            null
        )

        @SuppressLint("InflateParams")
        val mWindow = context.layoutInflater.inflate(
            R.layout.view_maps_info_window_custom,
            null
        )

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
                view.performLongClickHapticFeedback()
            }
        }
    }

    override fun onInfoWindowClick(p0: Marker) {
        binding.root.snackbar(R.string.app_name)
    }

    override fun onInfoWindowLongClick(marker: Marker) {
        val parkingSpot = Gson().fromJson(marker.snippet, ParkingSpot::class.java)
        if (ConstantFirebase.AVAILABILITY_STATUS.AVAILABLE.name.equals(
                parkingSpot.availabilityStatus,
                true
            )
        ) {
            BookingDialogFragment(parkingSpot).show(requireActivity().supportFragmentManager, null)
        } else {
            view?.rootView?.snackbar(
                string = ERROR_PARKING_OCCUPIED,
                drawableId = R.drawable.ic_round_error_24,
                anchorViewId = anchorViewId,
                color = NoteColor.Error,
                vibrate = true
            )
        }
    }

    companion object {
        val TAG = MapFragment::class.java.simpleName
        const val ERROR_PARKING_OCCUPIED = "Oops! Parking spot is occupied."
    }
}