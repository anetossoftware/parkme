package com.anetos.parkme.view.activity

import android.os.Bundle
import com.anetos.parkme.R
import com.anetos.parkme.core.BaseActivity
import com.anetos.parkme.core.helper.*
import com.anetos.parkme.databinding.ActivityMainBinding
import com.anetos.parkme.view.widget.common.WorkInProgressBottomSheetDialog
import com.anetos.parkme.view.widget.home.HomeFragment
import com.anetos.parkme.view.widget.map.MapFragment
import com.anetos.parkme.view.widget.profile.ProfileDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableLoc()
        isAppUpdateAvailable()
        setupActivity()
        setupState()
        setupListeners()
    }

    private fun setupActivity() {
        if (SharedPreferenceHelper().getUser().bookedParkingId.isNullOrBlank()) {
            this.replaceFragmentWithTag(
                MapFragment.newInstance(this@MainActivity), R.id.container, null)
        } else {
            this.replaceFragmentWithTag(
                HomeFragment.newInstance(this@MainActivity), R.id.container, null)
        }

    }

    private fun setupState() {

    }

    private fun setupListeners() {

    }

    companion object {
        val TAG = this.javaClass.simpleName
    }
}