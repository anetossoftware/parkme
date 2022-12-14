package com.anetos.parkme.core

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.anetos.parkme.MyApplication
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
    //private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    val appUpdateManager = AppUpdateManagerFactory.create(MyApplication.getAppContext())

    // Returns an intent object that you use to check for an update.
    val appUpdateInfoTask = appUpdateManager.appUpdateInfo

    private val installStateUpdatedListener: InstallStateUpdatedListener =
        InstallStateUpdatedListener {
            object : InstallStateUpdatedListener {
                override fun onStateUpdate(installState: InstallState) {
                    when (installState.installStatus()) {
                        InstallStatus.DOWNLOADING -> {
                            val bytesDownloaded = installState.bytesDownloaded()
                            val totalBytesToDownload = installState.totalBytesToDownload()
                            // Show update progress bar.
                        }
                        // Log state or install the update.
                        InstallStatus.DOWNLOADED -> {
                            //popupSnackbarForCompleteUpdate()
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.app_update_text),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        InstallStatus.INSTALLED -> {
                            //When status updates are no longer needed, unregister the listener.
                            appUpdateManager.unregisterListener(this)
                        }
                        else -> Log.i("InstallStatus is ...", "" + installState.installStatus())
                    }
                }
            }
        }

    @ExperimentalCoroutinesApi
    fun isAppUpdateAvailable() {

        // Before starting an update, register a listener for updates.
        appUpdateManager.registerListener(installStateUpdatedListener)

        Log.i(TAG, "started......")
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Request the update.
                try {
                    Log.i(TAG, "IMMEDIATE started......")

                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        appUpdateType(),
                        this,
                        RequestCode.APP_UPDATE_REQUEST_CODE
                    )
                } catch (e: Exception) {
                    Log.i(TAG, "IMMEDIATE ${e}")
                    e.printStackTrace()
                }
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                    AppUpdateType.FLEXIBLE
                )
            ) {
                try {
                    Log.i(TAG, "FLEXIBLE started......")
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        appUpdateType(),
                        this,
                        RequestCode.APP_UPDATE_REQUEST_CODE
                    )
                } catch (e: Exception) {
                    Log.i(TAG, "FLEXIBLE ${e}")
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                //popupSnackbarForCompleteUpdate()
                Toast.makeText(
                    applicationContext,
                    getString(R.string.app_update_text),
                    Toast.LENGTH_LONG
                ).show()
            }
            try {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo, this, AppUpdateOptions.newBuilder(appUpdateType())
                            .setAllowAssetPackDeletion(false)
                            .build(), RequestCode.APP_UPDATE_REQUEST_CODE
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun appUpdateType(): Int {
        val type: Int = if (APPUPDATETYPE == getString(R.string.app_Update)) {
            AppUpdateType.FLEXIBLE
        } else {
            AppUpdateType.IMMEDIATE
        }
        return type
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        /*registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            onAppUpdateActivityResult(APP_UPDATE_REQUEST_CODE, result)
        }.launch(intent)*/
    }
    val appUpdateLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        onAppUpdateActivityResult(APP_UPDATE_REQUEST_CODE, result)
    }

    fun onAppUpdateActivityResult(requestCode: Int, result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                APP_UPDATE_REQUEST_CODE -> {

                }
            }
        } else {
            Toast.makeText(this, getString(R.string.app_update_failure_text), Toast.LENGTH_LONG).show()
        }
    }

    private var googleApiClient: GoogleApiClient? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    fun enableLoc() {
        LocationHelper.isLocationEnabled(this)
        if (!PermissionHelper.checkLocationPermission(this)) {
            PermissionHelper.requestLocationPermission(
                this,
                requestCode = RequestCode.LOCATION_PERMISSION_REQUEST_CODE,
                message = "App need the permission"
            )
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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

        /* val task: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
         task.addOnCompleteListener {
             it.result.locationSettingsStates
         }
         task.addOnCompleteListener(object : OnCompleteListener<LocationSettingsResponse> {
             override fun onComplete(task: Task<LocationSettingsResponse>) {
                 try {
                     var response: LocationSettingsResponse = task.getResult(ApiException::class.java)
                     // All location settings are satisfied. The client can initialize location requests here.

                 } catch (exception: ApiException) {
                     val status: Status = exception.status
                     when (status.statusCode) {
                         LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                             status.startResolutionForResult(
                                 this@BaseActivity,
                                 REQUEST_LOCATION
                             )
                         } catch (e: IntentSender.SendIntentException) {
                         }
                     }
                 }
             }
         });*/

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