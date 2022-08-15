package com.anetos.parkme.view.activity

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.anetos.parkme.R
import com.anetos.parkme.core.BaseActivity
import com.anetos.parkme.core.helper.SharedPreferenceHelper
import com.anetos.parkme.databinding.ActivityMainBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment }

    private val navController by lazy { navHostFragment.navController }

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
        if (SharedPreferenceHelper().getUser().bookedSpot?.bookedParkingId.isNullOrBlank()) {
            inflateGraphAndSetStartDestination(R.id.mapFragment)
        } else {
            inflateGraphAndSetStartDestination(R.id.homeFragment)
        }

    }

    private fun setupState() {

    }

    private fun setupListeners() {

    }

    private fun inflateGraphAndSetStartDestination(startDestination: Int, args: Bundle? = null) {
        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
            .apply { this.setStartDestination(startDestination) }
        navController.setGraph(graph, args)
    }

    override fun onBackPressed() {
        /*BackPressDialogFragment(
            this,
            BACK_PRESS_DIALOG_TITLE,
            BACK_PRESS_DIALOG_CONFIRMATION,
            BACK_PRESS_DIALOG_DISCRIPTION,
            BACK_PRESS_DIALOG_POSITIVE_BUTTON,
            BACK_PRESS_DIALOG_NEGATIVE_BUTTON
        ).onClickListener(object : BackPressDialogFragment.onBackPressClickListener {
            override fun onClick(backPressDialogFragment: BackPressDialogFragment) {
                finish()
            }
        }).show(this.supportFragmentManager, null)*/
    }

    companion object {
        val TAG = this.javaClass.simpleName
        const val BACK_PRESS_DIALOG_TITLE = "Confirmation"
        const val BACK_PRESS_DIALOG_CONFIRMATION = "Confirmation"
        const val BACK_PRESS_DIALOG_DISCRIPTION = "Are you sure you want to exit?"
        const val BACK_PRESS_DIALOG_POSITIVE_BUTTON = "YES"
        const val BACK_PRESS_DIALOG_NEGATIVE_BUTTON = "NO"
    }
}