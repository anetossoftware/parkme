package com.anetos.parkme.view.widget.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anetos.parkme.BuildConfig
import com.anetos.parkme.R
import com.anetos.parkme.core.helper.*
import com.anetos.parkme.core.helper.dialog.DialogsManager
import com.anetos.parkme.data.ConstantDelay
import com.anetos.parkme.data.ConstantFirebase
import com.anetos.parkme.data.model.BankCard
import com.anetos.parkme.data.model.BookedSpot
import com.anetos.parkme.data.model.User
import com.anetos.parkme.data.model.WalletCard
import com.anetos.parkme.databinding.DialogAddMoneyToWalletFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddMoneyToWalletDialogFragment : BaseDialogFragment() {
    private val firestore = FirebaseFirestore.getInstance()

    private val anchorViewId by lazy { R.id.btn_okay }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        DialogAddMoneyToWalletFragmentBinding.inflate(inflater, container, false).withBinding {
            setupState()
            setupListeners()
        }

    private fun DialogAddMoneyToWalletFragmentBinding.setupState() {
        tb.tvDialogTitle.text = context?.stringResource(R.string.add_money)
        btnOkay.text = BUTTON_TEXT
        if (BuildConfig.DEBUG)
            feedDebugData()
    }

    private fun DialogAddMoneyToWalletFragmentBinding.setupListeners() {
        btnOkay.setOnClickListener {
            addMoney()
        }
    }

    private fun DialogAddMoneyToWalletFragmentBinding.addMoney() {
        val amount = etAmount.text.toString().trim()
        val name = layoutBankCardInput.etName.text.toString().trim()
        val cardNumber = layoutBankCardInput.etBankCard.text.toString().trim()
        val expiryDate = layoutBankCardInput.etExpiry.text.toString().trim()
        val cvv = layoutBankCardInput.etCvv.text.toString().trim()
        if (amount.isEmpty() && name.isEmpty() && cardNumber.isEmpty() && expiryDate.isEmpty() && cvv.isEmpty()) {
            view?.rootView?.snackbar(
                stringId = R.string.details_missing,
                anchorViewId = anchorViewId,
                drawableId = R.drawable.ic_round_error_24,
                color = NoteColor.Error,
                vibrate = true
            )
            tilAmount.error = getString(R.string.empty_amount)
            layoutBankCardInput.tilName.error = getString(R.string.empty_name)
            layoutBankCardInput.tilBankCard.error = getString(R.string.empty_card_number)
            layoutBankCardInput.tilExpiry.error = getString(R.string.empty_expiry)
            layoutBankCardInput.tilCvv.error = getString(R.string.empty_cvv)
            return
        }
        if (amount.isEmpty()) {
            tilAmount.isErrorEnabled = true
            context?.let {
                tilAmount.error = getString(R.string.empty_amount)
            }
            return
        } else {
            if (amount.toDouble() <= 0.0) {
                context?.let {
                    tilAmount.error = getString(R.string.error_amount)
                }
                return
            }
            tilAmount.isErrorEnabled = false
        }
        if (name.isEmpty()) {
            layoutBankCardInput.tilName.isErrorEnabled = true
            context?.let {
                layoutBankCardInput.tilName.error = getString(R.string.empty_name)
            }
            return
        } else {
            layoutBankCardInput.tilName.isErrorEnabled = false
        }
        if (cardNumber.length < 16) {
            layoutBankCardInput.tilBankCard.isErrorEnabled = true
            context?.let {
                layoutBankCardInput.tilBankCard.error = getString(R.string.empty_card_number)
            }
            return
        } else {
            layoutBankCardInput.tilBankCard.isErrorEnabled = false
        }
        if (expiryDate.expiryDate()) {
            layoutBankCardInput.tilExpiry.error = getString(R.string.empty_expiry)
            return
        } else if (expiryDate.length < 5) {
            layoutBankCardInput.tilExpiry.error = getString(R.string.empty_expiry)
            return
        } else {
            layoutBankCardInput.tilExpiry.isErrorEnabled = false
        }
        if (cvv.length < 3) {
            layoutBankCardInput.tilCvv.error = getString(R.string.empty_cvv)
            return
        } else {
            layoutBankCardInput.tilCvv.isErrorEnabled = false
        }
        activity?.hideKeyboard(root)
        context?.let { DialogsManager.showProgressDialog(it) }
        val bankCard = BankCard()
        bankCard.nameOnCard = name
        bankCard.cardNumber = cardNumber
        bankCard.expiryDate = expiryDate
        bankCard.cvv = cvv

        val user = SharedPreferenceHelper().getUser()

        val walletCard = WalletCard()
        walletCard.nameOnCard = name
        walletCard.avilableBalance = amount.toDouble() + (user.walletCard?.avilableBalance ?: 0.0)

        val bookedSpot = BookedSpot()
        bookedSpot.bookedParkingId = user.bookedSpot?.bookedParkingId.toString()
        bookedSpot.bookedFrom = user.bookedSpot?.bookedFrom
        bookedSpot.bookedTill = user.bookedSpot?.bookedTill
        bookedSpot.bookedHours = user.bookedSpot?.bookedHours

        val updateUser = User()
        updateUser.name = user.name.toString()
        updateUser.countryNameCode = user.countryNameCode.toString()
        updateUser.countryCode = user.countryCode.toString()
        updateUser.mobileNumber = user.mobileNumber.toString()
        updateUser.emailAddress = user.emailAddress.toString()
        updateUser.address = user.address.toString()
        updateUser.role = user.role.toString()
        updateUser.bankCard = bankCard
        updateUser.walletCard = walletCard
        updateUser.userSubscribe = user.userSubscribe.toString()
        updateUser.bookedSpot = bookedSpot
        updateUser.insertedAt = Calendar.getInstance().timeInMillis
        withDelay {
            firestore.collection(ConstantFirebase.COLLECTION_USERS)
                .document(DataHelper.getUserIndex(SharedPreferenceHelper().getUser()))
                .set(updateUser)
                .addOnCompleteListener {
                    DialogsManager.dismissProgressDialog()
                    if (it.isSuccessful) {
                        SharedPreferenceHelper().saveUser(updateUser)
                        onClick?.onSuccess(updateUser)
                        view?.rootView?.snackbar(
                            stringId = R.string.money_added_success,
                            drawableId = R.drawable.ic_round_check_circle_24,
                            anchorViewId = anchorViewId,
                            color = NoteColor.Success,
                        )
                        this@AddMoneyToWalletDialogFragment::dismiss.withDelay(ConstantDelay.NAVIGATION_DELAY)
                    } else {
                        view?.rootView?.snackbar(
                            stringId = R.string.payment_failed,
                            drawableId = R.drawable.ic_round_error_24,
                            anchorViewId = anchorViewId,
                            color = NoteColor.Error,
                            vibrate = true
                        )
                    }
                }
                .addOnFailureListener {
                    DialogsManager.dismissProgressDialog()
                }
        }
    }

    var onClick: onClickListener? = null

    interface onClickListener {
        fun onClick(addMoneyToWalletDialogFragment: AddMoneyToWalletDialogFragment) {}
        fun onNavigationClick(addMoneyToWalletDialogFragment: AddMoneyToWalletDialogFragment) {}
        fun onSuccess(data: Any) {}
        fun onFailure(addMoneyToWalletDialogFragment: AddMoneyToWalletDialogFragment) {}
    }

    fun onClickListener(onClick: onClickListener): AddMoneyToWalletDialogFragment {
        this.onClick = onClick
        return this
    }

    fun DialogAddMoneyToWalletFragmentBinding.feedDebugData() {
        layoutBankCardInput.etName.setText("Dummy Name")
        layoutBankCardInput.etBankCard.setText("1234567890123456")
        layoutBankCardInput.etExpiry.setText("02/25")
        layoutBankCardInput.etCvv.setText("999")

    }

    companion object {
        const val BUTTON_TEXT = "Confirm"
    }
}