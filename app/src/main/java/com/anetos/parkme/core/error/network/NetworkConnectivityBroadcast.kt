package com.anetos.parkme.core.error.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NetworkConnectivityBroadcast : BroadcastReceiver() {

    /**@DEPRECATION, @UnsafeProtectedBroadcastReceiver: This class is used only for KitKat OS and below this version*/
    @Suppress("UnsafeProtectedBroadcastReceiver", "DEPRECATION")
    override fun onReceive(context: Context, intent: Intent) {
        NetworkConnectivityHandler.checkForConnection(context)
    }
}