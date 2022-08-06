package com.anetos.parkme.view.widget.login_register

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import com.anetos.parkme.BuildConfig
import com.anetos.parkme.R
import com.anetos.parkme.core.BaseActivity
import com.anetos.parkme.core.helper.*
import com.anetos.parkme.core.helper.dialog.DialogsManager
import com.anetos.parkme.core.isNetworkAvailable
import com.anetos.parkme.data.ConstantDelay
import com.anetos.parkme.data.ConstantFirebase
import com.anetos.parkme.data.SharePrefConstant
import com.anetos.parkme.data.model.User
import com.anetos.parkme.databinding.ActivityLoginBinding
import com.anetos.parkme.view.activity.MainActivity
import com.anetos.parkme.view.widget.common.BackPressDialogFragment
import com.anetos.parkme.view.widget.common.ConfirmationDialogFragment
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding

    private var verificationID: String? = null
    private var countDownTimer: CountDownTimer? = null
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialise()
        setListeners()
    }

    private fun initialise() {
        binding.textView.text = TITLE_PAGE
        binding.btnLoginReg.text = BUTTON_TITLE
        binding.tvTitle.text = LOGIN_TITLE
        displayNumberLayout(false)
        if (BuildConfig.DEBUG) {
            //binding.countryCode.setAutoDetectedCountry(true)
            binding.etMobile.setText(DUMMY_NUMBER)
        }
    }

    private fun setListeners() {
        binding.etOtp.afterTextChanged {
            if (it.length == 6)
                hideKeyboard(binding.root)
        }
        binding.btnLoginReg.setOnClickListener {
            if (binding.numberLayout.visibility == View.VISIBLE)
                sendOTP()
            else
                verifyOTP()
        }
        binding.resendOtp.setOnClickListener {
            sendOTP()
        }
        binding.editNumber.setOnClickListener {
            displayNumberLayout(false)
        }
    }

    override fun onBackPressed() {
        BackPressDialogFragment(
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
        }).show(this.supportFragmentManager, null)
    }

    override fun onDestroy() {
        stopTimer()
        super.onDestroy()
    }

    private fun sendOTP() {
        if (!isNetworkAvailable()) {
            binding.root.snackbar(R.string.no_internet, vibrate = true)
            return
        }
        val mobile: String = binding.etMobile.text.toString().trim()
        when {
            mobile.isEmpty() -> {
                binding.tilMobile.error = getString(R.string.empty_mobile_number)
            }
            else -> {
                DialogsManager.showProgressDialog(this)
                val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onCodeAutoRetrievalTimeOut(p0: String) {}

                    override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                        super.onCodeSent(p0, p1)
                        verificationID = p0
                        DialogsManager.dismissProgressDialog()
                        displayVerifyLayout()
                        startTimer()
                        binding.root.snackbar(R.string.code_sent)
                    }

                    override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                        if (phoneAuthCredential.smsCode != null)
                            binding.etOtp.setText(phoneAuthCredential.smsCode)

                        stopTimer()
                        login(phoneAuthCredential)
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        DialogsManager.dismissProgressDialog()
                        e.printStackTrace()
                        when (e) {
                            is FirebaseTooManyRequestsException -> binding.root.snackbar(
                                    stringId = R.string.too_many_verify_attempts,
                                    drawableId = R.drawable.ic_round_error_24,
                                    color = NoteColor.Error,
                                    vibrate = true
                                )
                            is FirebaseAuthException -> binding.root.snackbar(
                                    stringId = R.string.server_contact_failed,
                                    drawableId = R.drawable.ic_round_error_24,
                                    color = NoteColor.Error,
                                    vibrate = true
                                )
                            else -> binding.root.snackbar(
                                stringId = R.string.verification_failed,
                                drawableId = R.drawable.ic_round_error_24,
                                color = NoteColor.Error,
                                vibrate = true
                            )
                        }
                    }
                }
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber("+${binding.countryCode.selectedCountryCode}$mobile")
                    .setTimeout(30L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(callback)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }
    }

    private fun startTimer() {
        binding.timer.text = "20S"
        binding.resendOtp.isEnabled = false
        binding.resendOtp.setTextColor(ContextCompat.getColor(this, R.color.grey))
        countDownTimer = object : CountDownTimer(timeInterval * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timer.text = "${millisUntilFinished / 1000}S"
            }

            override fun onFinish() {
                stopTimer()
            }
        }.start()
    }

    private fun displayVerifyLayout() {
        binding.numberLayout.visibility = View.GONE
        binding.verificationLayout.visibility = View.VISIBLE
        binding.tvTitle.text = getString(R.string.verification_header)
        binding.etOtp.setText("")
        binding.btnLoginReg.text = getString(R.string.proceed)
        binding.btnLoginReg.visibility = View.VISIBLE
    }

    private fun login(credential: PhoneAuthCredential) {
        DialogsManager.showProgressDialog(this)
        if (auth.currentUser != null)
            auth.signOut()
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                when {
                    task.isSuccessful -> {
                        binding.root.snackbar(
                            stringId = R.string.verification_success,
                            drawableId = R.drawable.ic_round_check_circle_24,
                            color = NoteColor.Success,
                        )
                        firestore.collection(ConstantFirebase.COLLECTION_USERS)
                            .document(
                                DataHelper.getUserIndex(
                                    binding.countryCode.selectedCountryCode,
                                    binding.etMobile.text.toString().trim()
                                )
                            )
                            .get()
                            .addOnCompleteListener { task1: Task<DocumentSnapshot?> ->
                                DialogsManager.dismissProgressDialog()
                                if (task1.isSuccessful) {
                                    val document = task1.result
                                    if (document != null && document.exists()) {
                                        val user: User? = document.toObject(User::class.java)
                                        if (user != null) {
                                            SharedPreferenceHelper()
                                                .saveObjectToSharedPreference(
                                                    SharePrefConstant.keyUserDetails, user
                                                )
                                            ::navigateToMainActivity.withDelay()
                                        } else {
                                            ConfirmationDialogFragment(
                                                "Error",
                                                "Not Found",
                                                getString(R.string.failed_to_find_user),
                                                getString(R.string.ok),
                                            ).show(supportFragmentManager, null)
                                        }
                                    } else {
                                        ::navigateToRegisterWithDelay.withDelay()
                                    }
                                } else {
                                    ConfirmationDialogFragment(
                                        "Error",
                                        "Not Found",
                                        getString(R.string.data_fetching_failed),
                                        getString(R.string.ok),
                                    ).show(supportFragmentManager, null)
                                }
                            }
                    }
                    task.exception is FirebaseAuthInvalidCredentialsException -> {
                        DialogsManager.dismissProgressDialog()
                        binding.root.snackbar(
                            stringId = R.string.incorrect_code,
                            drawableId = R.drawable.ic_round_error_24,
                            color = NoteColor.Error,
                            vibrate = true
                        )
                    }
                    else -> {
                        DialogsManager.dismissProgressDialog()
                        binding.root.snackbar(
                            stringId = R.string.sign_in_failed,
                            drawableId = R.drawable.ic_round_error_24,
                            color = NoteColor.Error,
                            vibrate = true
                        )
                    }
                }
            }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun navigateToRegisterWithDelay() {
        RegisterDialogFragment(
            binding.countryCode.selectedCountryNameCode,
            binding.etMobile.text.toString().trim()
        ).onClickListener(object : RegisterDialogFragment.onClickListener {
            override fun onClick(registerDialogFragment: RegisterDialogFragment) {
                DialogsManager.showProgressDialog(this@LoginActivity)
            }

            override fun onFailure(registerDialogFragment: RegisterDialogFragment) {
                DialogsManager.dismissProgressDialog()
                binding.root.snackbar(
                    stringId = R.string.booking_failed,
                    drawableId = R.drawable.ic_round_error_24,
                    color = NoteColor.Error,
                    vibrate = true
                )
            }

            override fun onNavigationClick(registerDialogFragment: RegisterDialogFragment) {
                DialogsManager.dismissProgressDialog()
                ::navigateWithDelay.withDelay(ConstantDelay.NAVIGATION_DELAY)
            }
        }).show(this.supportFragmentManager, null)
        displayNumberLayout(true)
    }

    fun navigateWithDelay() {
        startActivity(Intent(this, MainActivity::class.java))
        this.finish()
    }

    private fun verifyOTP() {
        if (!isNetworkAvailable()) {
            binding.root.snackbar(
                stringId = R.string.no_internet,
                drawableId = R.drawable.ic_round_error_24,
                color = NoteColor.Error,
                vibrate = true
            )
            return
        }
        val valOTP: String = binding.etOtp.text.toString().trim { it <= ' ' }
        if (valOTP.isEmpty()) {
            binding.tilOtp.error = getString(R.string.empty_otp)
            return
        }
        if (verificationID == null) {
            DialogsManager.showErrorDialog(
                this,
                getString(R.string.error_occurred),
                getString(R.string.ok),
                null
            )
            return
        }
        stopTimer()
        DialogsManager.showProgressDialog(this)
        val credential = PhoneAuthProvider.getCredential(
            verificationID!!,
            binding.etOtp.text.toString().trim { it <= ' ' }
        )
        login(credential)
    }

    private fun displayNumberLayout(clearText: Boolean) {
        binding.numberLayout.visibility = View.VISIBLE
        binding.verificationLayout.visibility = View.GONE
        if (clearText)
            binding.etMobile.setText("")
        binding.tvTitle.text = LOGIN_TITLE
        binding.btnLoginReg.text = getString(R.string.continue_label)
        verificationID = null
        stopTimer()
    }

    private fun stopTimer() {
        binding.timer.text = ""
        binding.resendOtp.isEnabled = true
        binding.resendOtp.setTextColor(ContextCompat.getColor(this, R.color.black))
        countDownTimer?.cancel()
        countDownTimer = null
    }

    companion object {
        val AUTHUI_REQUEST_CODE = 1001
        var TAG = LoginActivity::class.qualifiedName
        const val BUTTON_TITLE = "Login/Register"
        const val TITLE_PAGE = "Welcome to parkme"
        const val LOGIN_TITLE = "Hi, Sign in here"
        private val timeInterval = 30L
        const val BACK_PRESS_DIALOG_TITLE = "Confirmation"
        const val BACK_PRESS_DIALOG_CONFIRMATION = "Confirmation"
        const val BACK_PRESS_DIALOG_DISCRIPTION = "Are you sure you want to exit?"
        const val BACK_PRESS_DIALOG_POSITIVE_BUTTON = "YES"
        const val BACK_PRESS_DIALOG_NEGATIVE_BUTTON = "NO"
        const val DUMMY_NUMBER = "1234567890"
    }
}