package com.anetos.parkme.core

import android.app.Activity
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
import com.anetos.parkme.data.RequestCode
import com.anetos.parkme.data.RequestCode.APP_UPDATE_REQUEST_CODE
import com.anetos.parkme.view.activity.MainActivity
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
    //private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    val appUpdateManager = AppUpdateManagerFactory.create(Application.getAppContext())

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
    private fun isAppUpdateAvailable() {

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

    protected fun setToolbar(viewId: Int, title: String?) {
        val toolbar = findViewById<Toolbar>(viewId)
        setSupportActionBar(toolbar)
        toolbar.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }

    protected fun showSnackBar(container: View?, message: String?, buttonText: String?) {
        val snackbar =
            Snackbar.make(container!!, message!!, BaseTransientBottomBar.LENGTH_INDEFINITE)
        snackbar.setAction(buttonText) { view: View? -> snackbar.dismiss() }
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.white))
        snackbar.show()
    }

    val isConnectedToInternet: Boolean
        get() {
            val cm = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }

    fun hideKeyboard() {
        val view = currentFocus
        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    companion object {
        val TAG = MainActivity::class.qualifiedName
        const val APPUPDATETYPE = "FLEXIBLE"
    }
}