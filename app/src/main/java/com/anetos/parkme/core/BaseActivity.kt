package com.anetos.parkme.core

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.anetos.parkme.Application
import com.anetos.parkme.R
import com.anetos.parkme.core.helper.PermissionHelper
import com.anetos.parkme.core.maphelper.LocationHelper
import com.anetos.parkme.data.RequestCode
import com.anetos.parkme.data.RequestCode.APP_UPDATE_REQUEST_CODE
import com.anetos.parkme.view.activity.MainActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * [BaseActivity]
 * @author
 * created by Jaydeep Bhayani on 01/01/2022
 */
abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    private var googleApiClient: GoogleApiClient? = null
    private var googleApi: FusedLocationProviderClient? = null

    fun enableLoc() {
        LocationHelper.isLocationEnabled(this)
        if (!PermissionHelper.checkLocationPermission(this)) {
            PermissionHelper.requestLocationPermission(
                this,
                requestCode = RequestCode.LOCATION_PERMISSION_REQUEST_CODE,
                message = "App need the permission"
            )
        }
        googleApi = FusedLocationProviderClient(this)
        googleApiClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                override fun onConnected(bundle: Bundle?) {}
                override fun onConnectionSuspended(i: Int) {
                    googleApiClient?.connect()
                }
            })
            .addOnConnectionFailedListener {
            }.build()
        googleApiClient?.connect()

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 30 * 1000.toLong()
        locationRequest.fastestInterval = 5 * 1000.toLong()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient!!, builder.build())
        result.setResultCallback { results ->
            val status: Status = results.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    status.startResolutionForResult(
                        this@BaseActivity,
                        RequestCode.REQUEST_LOCATION
                    )
                } catch (e: IntentSender.SendIntentException) {
                }
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RequestCode.REQUEST_LOCATION -> when (resultCode) {
                Activity.RESULT_OK -> Log.d("abc", "OK")
                Activity.RESULT_CANCELED -> Log.d("abc", "CANCEL")
            }
        }
    }

    companion object {
        val TAG = MainActivity::class.qualifiedName
        const val APPUPDATETYPE = "FLEXIBLE"
    }
}