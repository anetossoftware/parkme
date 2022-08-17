package com.anetos.parkme.core.error.network

import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import android.os.PowerManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

object NetworkConnectivityHandler {

    private var connectivityManagerCallback: ConnectivityManager.NetworkCallback? = null
    private var networkConnected = true
    fun register(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (connectivityManagerCallback == null) {
                createNetworkCallback(context)
                registerConnectionManagerCallback(context)
            }
        }
        checkForConnection(context)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun registerConnectionManagerCallback(context: Context) {
        connectivityManagerCallback?.let {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), it)
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun createNetworkCallback(context: Context) {
        connectivityManagerCallback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                if (!networkConnected) {
                    networkConnected = true
                    handleNetworkConnection(hasNetworkConnection = true, context = context)
                }
            }

            override fun onLost(network: Network) {
                networkConnected = false
                handleNetworkConnection(hasNetworkConnection = false, context = context)
            }
        }
    }

    fun checkForConnection(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            && powerManager?.isPowerSaveMode == true
        ) {
            GlobalScope.launch {
                delay(500L)
                handleNetworkConnection(isNetworkConnected(context), context)
            }
        } else {
            handleNetworkConnection(isNetworkConnected(context), context)
        }
    }

    private fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return connectivityManager?.let {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                @Suppress("DEPRECATION")
                connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true
            } else {
                connectivityManager.activeNetwork != null
            }
        } ?: false
    }


    fun handleNetworkConnection(hasNetworkConnection: Boolean, context: Context) {
        if (!hasNetworkConnection) {
            onNoInternetConnection(context)
        } else {
            onInternetConnected()
        }
    }

    private fun onNoInternetConnection(context: Context) {
        val networkConnectivityHelper = NetworkConnectivityHelper(WeakReference(context))
        val error = networkConnectivityHelper.getConsolidatedErrorObject()

        //EventHelper.publish(EventObserverList.BLOCK_UI.observer(), null)
        //EventHelper.publish(EventObserverList.SHOW_GENERIC_ERROR_POPUP_FROM_ACTIVITY.observer, Gson().toJson(error))
        //EventHelper.publish(EventObserverList.SHOW_GENERIC_ERROR_POPUP_FROM_DIALOG.observer, Gson().toJson(error))
    }

    private fun onInternetConnected() {
        //EventHelper.publish(EventObserverList.UN_BLOCK_UI.observer(), null)
    }
}