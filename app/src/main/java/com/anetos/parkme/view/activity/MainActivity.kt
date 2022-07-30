package com.anetos.parkme.view.activity

import android.os.Bundle
import com.anetos.parkme.R
import com.anetos.parkme.core.BaseActivity
import com.anetos.parkme.core.helper.replaceFragmentWithTag
import com.anetos.parkme.core.helper.setOnSwipeGestureListener
import com.anetos.parkme.databinding.ActivityMainBinding
import com.anetos.parkme.view.widget.common.WorkInProgressBottomSheetDialog
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
        setupBaseDialogFragment()
        setupState()
        setupListeners()
        this.replaceFragmentWithTag(
            MapFragment.newInstance(this@MainActivity),
            R.id.container,
            null
        )
    }

    private fun setupBaseDialogFragment() {
        /*binding.bottomAppBar.setRoundedCorners()
        this.let { context ->
            val backgroundColor = context.colorAttributeResource(R.attr.noteBackgroundColor)
            binding.bottomAppBar.navigationIcon?.mutate()?.setTint(backgroundColor)
            binding.bottomAppBar.menu.forEach { it.icon?.mutate()?.setTint(backgroundColor) }
        }*/
        binding.bottomAppBar.setOnSwipeGestureListener {

        }
    }

    private fun setupState() {

    }

    private fun setupListeners() {
        binding.bottomAppBar.setNavigationOnClickListener {
            WorkInProgressBottomSheetDialog().show(
                supportFragmentManager,
                null
            )
        }
        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.wallet -> {

                    true
                }
                R.id.profile -> {
                    //setupFadeTransition()
                    showProfile()
                    true
                }
                R.id.more -> {
                    WorkInProgressBottomSheetDialog().show(supportFragmentManager, null)
                    true
                }
                else -> false
            }
        }
    }

    fun showProfile() {
        ProfileDialogFragment().show(
            supportFragmentManager,
            null
        )
    }

    companion object {
        val TAG = this.javaClass.simpleName
    }
}