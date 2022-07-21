package com.anetos.parkme.view.widget.login_register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.anetos.parkme.R
import com.anetos.parkme.core.BaseActivity
import com.anetos.parkme.databinding.ActivityLoginBinding
import com.anetos.parkme.view.activity.MainActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class LoginRegisterActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Objects.requireNonNull(supportActionBar!!).hide()

        binding.textView.text = TITLE_PAGE
        binding.btnLoginReg.text = BUTTON_TITLE

        binding.btnLoginReg.setOnClickListener { view ->
            handleLogin()
        }
    }

    fun handleLogin() {

        val provider = Arrays.asList(
            //EmailBuilder().build(),
            GoogleBuilder().build()
        )
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(provider)
            .setLogo(R.drawable.ic_round_person_24)
            .setAlwaysShowSignInMethodScreen(false) // avoid default login screenh
            .build()

        signInLauncher.launch(signInIntent)

        /*registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            onActivityResult(AUTHUI_REQUEST_CODE, result)
        }.launch(intent)*/
    }

     val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
         onActivityResult(AUTHUI_REQUEST_CODE, result)
     }

    fun onActivityResult(requestCode: Int, result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                AUTHUI_REQUEST_CODE -> {
                    // We have signed in the user or we have a new user

                    // We have signed in the user or we have a new user
                    val user = FirebaseAuth.getInstance().currentUser
                    Log.d(TAG, "onActivityResult: " + user!!.email)

                    if (user.metadata!!.creationTimestamp == user.metadata!!.lastSignInTimestamp) {
                        Toast.makeText(this, "Welcome New User", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Welcome Back Again", Toast.LENGTH_SHORT).show()
                    }

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        } else {
            // Signed in failed
            val response = IdpResponse.fromResultIntent(result.data)
            if (response == null) {
                Log.d(TAG, "onActivityResult: The user has cancelled the signed in request")
            } else {
                Log.d(TAG, "onActivityResult: ", response.error)
            }
        }
    }


    companion object {
        val AUTHUI_REQUEST_CODE = 1001
        var TAG = LoginRegisterActivity::class.qualifiedName
        const val BUTTON_TITLE = "Login/Register"
        const val TITLE_PAGE = "Welcome to my note"
    }
}