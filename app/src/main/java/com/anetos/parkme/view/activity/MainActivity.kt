package com.anetos.parkme.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anetos.parkme.R
import com.anetos.parkme.core.helper.replaceFragmentWithTag
import com.anetos.parkme.databinding.ActivityMainBinding
import com.anetos.parkme.view.HomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.replaceFragmentWithTag(HomeFragment.newInstance(this@MainActivity), R.id.container, null)
    }
}