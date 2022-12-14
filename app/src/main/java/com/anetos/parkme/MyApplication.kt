package com.anetos.parkme

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.google.android.gms.maps.MapsInitializer
import com.google.android.libraries.places.api.Places
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp
import java.util.*

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = applicationContext

        FirebaseApp.initializeApp(this)
        //FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false)

        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.setAnalyticsCollectionEnabled(true)
        MapsInitializer.initialize(this)
        Places.initialize(this, getString(R.string.google_maps_key), Locale.US)
    }

    fun getAppContext(): Context {
        return context
    }

    /*fun getClientDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }*/

    companion object {
        lateinit var instance: Application
        lateinit var firebaseAnalytics: FirebaseAnalytics
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        @Synchronized
        fun getAppContext(): Application {
            return instance
        }
    }
}