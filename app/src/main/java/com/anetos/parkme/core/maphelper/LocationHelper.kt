package com.anetos.parkme.core.maphelper

import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import com.anetos.parkme.core.helper.PermissionHelper.checkLocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

object LocationHelper {
    fun fusedLocation(
        context: Context,
        fusedLocationClient: FusedLocationProviderClient
    ): Location? {
        var lastLocation: Location? = null
        checkLocationPermission(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastLocation = location
            }
        }
        return lastLocation
    }

    fun fusedLocation(activity: Activity): Location? {
        var lastLocation: Location? = null
        checkLocationPermission(activity)
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastLocation = location
            }
        }
        return lastLocation
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationMode: Int
        val locationProviders: String

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(
                    context.contentResolver,
                    Settings.Secure.LOCATION_MODE
                )

            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
                return false
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF

        } else {
            locationProviders = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED
            )
            return !TextUtils.isEmpty(locationProviders)
        }
    }
}