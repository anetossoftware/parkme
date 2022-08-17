package com.anetos.parkme.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.anetos.parkme.core.helper.Navigator
import com.anetos.parkme.core.helper.SharedPreferenceHelper
import com.anetos.parkme.databinding.ActivitySplashBinding
import com.google.firebase.messaging.FirebaseMessaging

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkAndSaveFirebaseToken()
        Looper.myLooper()?.let {
            Handler(it).postDelayed({
                binding.animationView.pauseAnimation()
                proceed()
            }, DELAY)
        }
    }

    private fun checkAndSaveFirebaseToken() {
        if (SharedPreferenceHelper().getValueString(SharedPreferenceHelper.keyFirebaseToken) == null) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (it.isSuccessful)
                    SharedPreferenceHelper().saveAppData(
                        SharedPreferenceHelper.keyFirebaseToken,
                        it.result
                    )
            }
        }
    }

    private fun proceed() {
        if (SharedPreferenceHelper().containsUserKey(SharedPreferenceHelper.keyUserDetails)) {
            val user = SharedPreferenceHelper().getUser()
            when {
                user.isUser() -> Navigator.toMainActivity()
            }
        } else
            Navigator.toLoginActivity()
        finish()
    }

    companion object {
        val DELAY = 1200L
    }
}