package com.anetos.parkme.view.widget.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anetos.parkme.R
import com.anetos.parkme.core.helper.*
import com.anetos.parkme.data.ConstantFirebase
import com.anetos.parkme.data.model.User
import com.anetos.parkme.databinding.DialogFragmentProfileBinding
import com.anetos.parkme.view.activity.MainActivity
import com.google.firebase.firestore.FirebaseFirestore

class ProfileDialogFragment() : BaseDialogFragment() {
    private lateinit var binding: DialogFragmentProfileBinding
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentProfileBinding.inflate(inflater, container, false)
        setupBaseDialogFragment()
        setupState()
        setupListeners()
        return binding.root
    }

    private fun setupBaseDialogFragment() =
        binding.apply {
            tb.tvDialogTitle.text = DIALOG_TITLE
            btnRegister.text = DIALOG_TITLE
            //countryCode.setDefaultCountryUsingNameCode(this@ProfileDialogFragment.countryCode)
            //countryCode.resetToDefaultCountry(mobileNumber)
            //etMobile.setText(mobileNumber)
            etMobile.disableLook()
        }

    private fun setupState() {

    }

    private fun setupListeners() {
        binding.apply {
            btnRegister.setOnClickListener {
                registerUser()
            }
        }
    }

    private fun registerUser() {
        binding.apply {
            val name = etName.text.toString().trim()
            val number = etMobile.text.toString().trim()
            val email = etEmail.text.toString().trim()
            if (name.isEmpty() || email.isEmpty() || number.isEmpty()) {
                view?.snackbar(R.string.details_missing)
                tilName.error = getString(R.string.empty_name)
                tilEmail.error = getString(R.string.empty_email)
                return
            }
            if (name.isEmpty()) {
                tilName.isErrorEnabled = true
                context?.let {
                    tilName.error = getString(R.string.empty_name)
                }
                return
            }
            if (email.indexOf("@") < 0 || email.indexOf(".") < 0) {
                tilName.error = getString(R.string.invalid_email)
                return
            }
            activity?.hideKeyboard(root)
            val user = User(
                name,
                countryCode.selectedCountryNameCode,
                "+${countryCode.selectedCountryCode}",
                number,
                email,
                ConstantFirebase.ROLES.REGULAR.name
            )
            firestore.collection(ConstantFirebase.COLLECTION_USERS)
                .document(DataHelper.getUserIndex(user))
                .set(user)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        view?.snackbar(R.string.registered)
                        //FirebaseAuth.getInstance().signOut()
                        startActivity(Intent(activity, MainActivity::class.java))
                        activity?.finish()
                    } else
                        view?.snackbar(R.string.register_failed)
                }
        }
    }

    companion object {
        const val DIALOG_TITLE = "Profile"
        const val BACK_PRESS_DIALOG_TITLE = "Confirmation"
        const val BACK_PRESS_DIALOG_CONFIRMATION = "Are you sure you want to exit?"
        const val BACK_PRESS_DIALOG_DISCRIPTION =
            "You must enter asked detail to access the app features"
        const val BACK_PRESS_DIALOG_POSITIVE_BUTTON = "YES"
        const val BACK_PRESS_DIALOG_NAGATIVE_BUTTON = "NO"
    }
}