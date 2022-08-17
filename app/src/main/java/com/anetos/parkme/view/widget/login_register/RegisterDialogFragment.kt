package com.anetos.parkme.view.widget.login_register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anetos.parkme.Application
import com.anetos.parkme.BuildConfig
import com.anetos.parkme.R
import com.anetos.parkme.core.helper.*
import com.anetos.parkme.data.ConstantFirebase
import com.anetos.parkme.data.SharePrefConstant
import com.anetos.parkme.data.model.User
import com.anetos.parkme.databinding.RegisterDialogFragmentBinding
import com.anetos.parkme.view.widget.common.BackPressDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class RegisterDialogFragment(
    val countryCode: String,
    val mobileNumber: String
) : BaseDialogFragment() {
    private lateinit var binding: RegisterDialogFragmentBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val anchorViewId by lazy { R.id.btnRegister }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isCancelable = false
        binding = RegisterDialogFragmentBinding.inflate(inflater, container, false)
        setupBaseDialogFragment()
        setupState()
        setupListeners()
        return binding.root
    }

    private fun setupBaseDialogFragment() =
        binding.apply {
            tb.tvDialogTitle.text = DIALOG_TITLE
            btnRegister.text = DIALOG_TITLE
            countryCode.setDefaultCountryUsingNameCode(this@RegisterDialogFragment.countryCode)
            //countryCode.resetToDefaultCountry(mobileNumber)
            etMobile.setText(mobileNumber)
            etMobile.disableLook()
            if (BuildConfig.DEBUG)
                feedDebugData()
        }

    private fun setupState() {

    }

    private fun setupListeners() {
        binding.apply {
            btnRegister.setOnClickListener {
                registerUser()
            }
            tb.ivClose.setOnClickListener {
                activity?.supportFragmentManager?.let {
                    BackPressDialogFragment(
                        Application.context,
                        BACK_PRESS_DIALOG_TITLE,
                        BACK_PRESS_DIALOG_CONFIRMATION,
                        BACK_PRESS_DIALOG_DISCRIPTION,
                        BACK_PRESS_DIALOG_POSITIVE_BUTTON,
                        BACK_PRESS_DIALOG_NAGATIVE_BUTTON
                    ).onClickListener(object : BackPressDialogFragment.onBackPressClickListener {
                        override fun onClick(backPressDialogFragment: BackPressDialogFragment) {
                            this@RegisterDialogFragment.dismiss()
                        }
                    }).show(it, null)
                }
            }
        }
    }

    private fun registerUser() {
        binding.apply {
            val name = etName.text.toString().trim()
            val number = etMobile.text.toString().trim()
            val email = etEmail.text.toString().trim()
            if (name.isEmpty() && email.isEmpty() && number.isEmpty()) {
                view?.rootView?.snackbar(
                    stringId = R.string.details_missing,
                    anchorViewId = anchorViewId,
                    drawableId = R.drawable.ic_round_error_24,
                    color = NoteColor.Error,
                    vibrate = true
                )
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
            } else {
                tilName.isErrorEnabled = false
            }
            if (email.indexOf("@") < 0 || email.indexOf(".") < 0) {
                tilEmail.isErrorEnabled = true
                context?.let {
                    tilEmail.error = getString(R.string.invalid_email)
                }
                return
            } else {
                tilEmail.isErrorEnabled = false
            }
            activity?.hideKeyboard(root)
            onClick?.onClick(this@RegisterDialogFragment)
            val user = User()
            user.name = name
            user.countryNameCode = countryCode.selectedCountryNameCode
            user.countryCode = "+${countryCode.selectedCountryCode}"
            user.mobileNumber = number
            user.emailAddress = email
            user.insertedAt = Calendar.getInstance().timeInMillis
            ConstantFirebase.ROLES.REGULAR.name
            withDelay {
                firestore.collection(ConstantFirebase.COLLECTION_USERS)
                    .document(DataHelper.getUserIndex(user))
                    .set(user)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            SharedPreferenceHelper().saveObjectToSharedPreference(
                                SharePrefConstant.keyUserDetails,
                                user
                            )
                            view?.rootView?.snackbar(
                                stringId = R.string.registered,
                                anchorViewId = anchorViewId,
                                drawableId = R.drawable.ic_round_check_circle_24,
                                color = NoteColor.Success,
                            )
                            withDelay(900L) {
                                dismiss()
                            }
                            onClick?.onNavigationClick(this@RegisterDialogFragment)
                        }
                    }
                    .addOnFailureListener {
                        onClick?.onFailure(this@RegisterDialogFragment)
                    }
            }

        }
    }

    fun navigationDelay() {

    }

    fun feedDebugData() {
        binding.apply {
            etName.setText("Dummy Name")
            etEmail.setText("dummy@gmail.com")
        }
    }

    var onClick: onClickListener? = null

    interface onClickListener {
        fun onClick(registerDialogFragment: RegisterDialogFragment)
        fun onFailure(registerDialogFragment: RegisterDialogFragment)
        fun onNavigationClick(registerDialogFragment: RegisterDialogFragment)
    }

    fun onClickListener(onClick: onClickListener): RegisterDialogFragment {
        this.onClick = onClick
        return this
    }

    companion object {
        const val DIALOG_TITLE = "Register"
        const val BACK_PRESS_DIALOG_TITLE = "Confirmation"
        const val BACK_PRESS_DIALOG_CONFIRMATION = "Are you sure you want to exit?"
        const val BACK_PRESS_DIALOG_DISCRIPTION =
            "You must enter the asked details to access the app"
        const val BACK_PRESS_DIALOG_POSITIVE_BUTTON = "YES"
        const val BACK_PRESS_DIALOG_NAGATIVE_BUTTON = "NO"
    }
}