package com.anetos.parkme.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.anetos.parkme.databinding.ActivitySplashBinding
import com.anetos.parkme.view.activity.MainActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Looper.myLooper()?.let {
            Handler(it).postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, DELAY)
        }
    }

    companion object {
        val DELAY = 2600L
    }
}