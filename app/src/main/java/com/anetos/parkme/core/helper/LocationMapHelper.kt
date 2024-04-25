package com.anetos.parkme.core.helper

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class LocationMapHelper(var activity: Activity, var context: Context) {

    private val mLocationRequestCode = 1
    private var mLocationRequest: LocationRequest = LocationRequest()
    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val updateInterval: Long = 10 * 1000
    private val fastestInterval: Long = 2000

    init {
        startLocationUpdate()
    }

    private fun startLocationUpdate() {
        checkLocationPermission(activity)

        //Create the location request to start receiving updates
        mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = updateInterval
        mLocationRequest.fastestInterval = fastestInterval

        // Create LocationSettingsRequest object using location request
        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest: LocationSettingsRequest = builder.build()

        val settingsClient: SettingsClient = LocationServices.getSettingsClient(context)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        // requestLocationPermission(this, null, 1, "permission")
        LocationServices.getFusedLocationProviderClient(activity).requestLocationUpdates(
            mLocationRequest,
            locationUpdates,
            null
        )
    }

    private val locationUpdates = object : LocationCallback() {
        override fun onLocationResult(lr: LocationResult) {
            lr.lastLocation?.let { setLocation(it.latitude, it.longitude) }
            fusedLocationClient.removeLocationUpdates(this)
        }
    }

    private fun setLocation(latitude: Double, longitude: Double) {
        val locationData =
            Location(latitude = latitude.toString(), longitude = longitude.toString())
        SharedPreferenceHelper()
            .saveObjectToSharedPreference("LOCATION", locationData)
    }

    private fun checkLocationPermission(activity: Activity) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                mLocationRequestCode
            )
            return
        }
    }
}

class Location(
    var id: Int = 0,
    var name: String = "",
    var latitude: String = "",
    var longitude: String = ""
)