package com.anetos.parkme

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class Application : Application() {

    var context: Context? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = applicationContext

        FirebaseApp.initializeApp(this)
        //FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false)

        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.setAnalyticsCollectionEnabled(true)
    }

    fun getAppContext(): Context? {
        return context
    }

    /*fun getClientDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }*/

    companion object {
        lateinit var instance: Application
        lateinit var firebaseAnalytics: FirebaseAnalytics

        @Synchronized
        fun getAppContext(): Application {
            return instance
        }
    }
}