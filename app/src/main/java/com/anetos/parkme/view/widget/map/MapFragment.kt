package com.anetos.parkme.view.widget.map

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.anetos.parkme.core.BaseFragment
import com.anetos.parkme.core.helper.PermissionHelper
import com.anetos.parkme.core.helper.snackbar
import com.anetos.parkme.core.maphelper.*
import com.anetos.parkme.data.ConstantFirebase
import com.anetos.parkme.data.model.ParkingSpot
import com.anetos.parkme.databinding.FragmentMapBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.maps.android.clustering.ClusterManager
import kotlinx.android.synthetic.main.layout_maps.*
import java.io.IOException
import java.util.*
import com.anetos.parkme.R
import com.anetos.parkme.core.helper.vectorToBitmap
import com.anetos.parkme.view.widget.home.HomeFragment


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
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnInfoWindowClickListener(this)
        mMap.setOnInfoWindowLongClickListener(this);
        mClusterManager = ClusterManager(requireContext(), mMap)
        context?.configureMap(mMap)
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

        //requestLocationPermission(this, null, 1, "permission")
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
                for (document in result) {
                    val data = document.data
                    val parkingSpot = ParkingSpot()
                    parkingSpot.documentId = document.id
                    parkingSpot.parkingId = data.get(ParkingSpot::parkingId.name).toString()
                    parkingSpot.address = data.get(ParkingSpot::address.name).toString()
                    parkingSpot.latitude = data.get(ParkingSpot::latitude.name) as Double
                    parkingSpot.longitude = data.get(ParkingSpot::longitude.name) as Double
                    parkingSpot.pricePerHr = data.get(ParkingSpot::pricePerHr.name) as Double
                    parkingSpot.availabilityStatus = data.get(ParkingSpot::availabilityStatus.name).toString()
                    Log.d(TAG, "${document.id} => ${document.data}")
                    parkingSpotList.add(parkingSpot)
                }
                //stopShimmering()
                if (parkingSpotList.size == 0 || parkingSpotList.isNullOrEmpty()) {
                    parkingSpotList.add(ParkingSpot()) // for empty-list placeholder
                }

                //mClusterManager.addMarkers(requireActivity(), mMap, parkingSpotList)
                mClusterManager.addMarkers(parkingSpotList)
            }
            .addOnFailureListener { exception ->
                //binding.rvNotes.hide()
                //binding.placeholder.tvPlaceholder.show()
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
            render(marker, mWindow);
            return mWindow
        }

        private fun render(marker: Marker, view: View) {
            val place = Gson().fromJson(marker.snippet, ParkingSpot::class.java)
            if (place != null) {
                view.findViewById<TextView>(R.id.locationName).text = place.address
                view.findViewById<TextView>(R.id.price).text =
                    place.pricePerHr.toString().plus("/hr")
                view.findViewById<TextView>(R.id.provider).text = place.address
            }
        }
    }

    override fun onInfoWindowClick(p0: Marker) {
        binding.root.snackbar(R.string.app_name)
        Toast.makeText(
            requireActivity(), "Info window clicked",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onInfoWindowLongClick(marker: Marker) {
        val parkingSpot = marker.snippet
        val fragment: Fragment = HomeFragment()
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
        binding.root.snackbar(R.string.app_name)

        Toast.makeText(
            requireActivity(), "onInfoWindowLongClick Info window clicked",
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        fun newInstance(ctx: Context) = MapFragment()
        val TAG = this.javaClass.simpleName
    }
}