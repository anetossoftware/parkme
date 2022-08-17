package com.anetos.parkme.view.activity

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.anetos.parkme.R
import com.anetos.parkme.core.BaseActivity
import com.anetos.parkme.core.helper.DataHelper
import com.anetos.parkme.core.helper.Navigator
import com.anetos.parkme.core.helper.SharedPreferenceHelper
import com.anetos.parkme.core.helper.dialog.DialogsManager
import com.anetos.parkme.data.ConstantFirebase
import com.anetos.parkme.data.model.User
import com.anetos.parkme.databinding.ActivityMainBinding
import com.anetos.parkme.view.widget.common.ConfirmationDialogFragment
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment }

    private val navController by lazy { navHostFragment.navController }

    val db = Firebase.firestore

    override fun onStart() {
        super.onStart()
        reloadUser()
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableLoc()
        setupState()
        setupListeners()
    }

    private fun setupState() {
        if (SharedPreferenceHelper().getUser().bookedSpot?.bookedParkingId.isNullOrBlank()) {
            inflateGraphAndSetStartDestination(R.id.mapFragment)
        } else {
            inflateGraphAndSetStartDestination(R.id.homeFragment)
        }
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

    fun reloadUser() {
        db.collection(ConstantFirebase.COLLECTION_USERS)
            .document(
                DataHelper.getUserIndex(
                    SharedPreferenceHelper().getUser().countryCode.toString(),
                    SharedPreferenceHelper().getUser().mobileNumber.toString()
                )
            )
            .get()
            .addOnCompleteListener { task1: Task<DocumentSnapshot?> ->
                DialogsManager.dismissProgressDialog()
                if (task1.isSuccessful) {
                    val document = task1.result
                    if (document != null && document.exists()) {
                        val newUser: User? = document.toObject(User::class.java)
                        if (newUser != null) {
                            SharedPreferenceHelper().clearAppPreferences()
                            SharedPreferenceHelper().saveUser(newUser)
                        }
                    }
                }
            }
    }

    fun resetUser() {
        ConfirmationDialogFragment(
            dialogTitle = "Occupancy Update",
            confirmation = "Force Occupancy Update",
            description = "It seems your parking spot has some updates related to occupancy, please OKAY to settled the occupied spot.",
            buttonText = "OKAY!",
            isCancelable = false
        ).onClickListener(object : ConfirmationDialogFragment.onConfirmationClickListener {
            override fun onClick(confirmationDialogFragment: ConfirmationDialogFragment) {
                Navigator.toMainActivity(true)
            }
        }).show(this.supportFragmentManager, null)
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