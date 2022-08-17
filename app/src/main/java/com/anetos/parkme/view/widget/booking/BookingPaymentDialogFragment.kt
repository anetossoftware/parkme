package com.anetos.parkme.view.widget.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anetos.parkme.Application
import com.anetos.parkme.BuildConfig
import com.anetos.parkme.R
import com.anetos.parkme.core.helper.*
import com.anetos.parkme.core.helper.dialog.DialogsManager
import com.anetos.parkme.data.ConstantDelay
import com.anetos.parkme.data.ConstantFirebase
import com.anetos.parkme.data.model.*
import com.anetos.parkme.databinding.DialogFragmentBookingPaymentBinding
import com.anetos.parkme.view.widget.common.BackPressDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class BookingPaymentDialogFragment(
    var parkingSpot: ParkingSpot? = null,
    var bookedSpot: BookedSpot? = null,
) : BaseDialogFragment() {
    private lateinit var binding: DialogFragmentBookingPaymentBinding

    private val firestore = FirebaseFirestore.getInstance()

    private val anchorViewId by lazy { R.id.btn_confirm }

    var isWalletSelected = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isCancelable = false
        binding = DialogFragmentBookingPaymentBinding.inflate(inflater, container, false)
        setupBaseDialogFragment()
        setupState()
        setupListeners()
        return binding.root
    }

    private fun setupBaseDialogFragment() {
        binding.apply {
            tb.tvDialogTitle.text = DIALOG_TITLE
            btnConfirm.text = DIALOG_TITLE
            if (BuildConfig.DEBUG)
                feedDebugData()
        }
    }

    private fun setupState() {
        binding.apply {
            tvPayableAmount.text = String.format(
                TOTAL_AMOUNT, formatAmount(
                    Currency.getInstance(
                        Locale.CANADA
                    ), getTotalPrice()
                )
            )
            setupWalletCard(SharedPreferenceHelper().getUser().walletCard)
        }
    }

    private fun setupListeners() {
        binding.apply {
            tlPaymentLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                }

                override fun onTabSelected(tab: TabLayout.Tab) {
                    if (getString(R.string.bank_card).equals(tab.text.toString(), true)) {
                        isWalletSelected = false
                        layoutBankCardInput.root.visible()
                        layoutWallet.root.gone()
                    } else if (getString(R.string.wallet).equals(tab.text.toString(), true)) {
                        isWalletSelected = true
                        layoutWallet.root.visible()
                        layoutWallet.efabAddMoney.hide()
                        layoutBankCardInput.root.gone()
                    }
                }
            })
            btnConfirm.setOnClickListener {
                bookParking()
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
                            this@BookingPaymentDialogFragment.dismiss()
                        }
                    }).show(it, null)
                }
            }
        }
    }

    fun DialogFragmentBookingPaymentBinding.setupWalletCard(walletCard: WalletCard?) {
        layoutWallet.apply {
            tvValueBalance.text = (formatAmount(
                Currency.getInstance(Locale.CANADA),
                walletCard?.avilableBalance ?: 0.0
            ))
        }
    }

    private fun bookParking() {
        binding.apply {
            val name = layoutBankCardInput.etName.text.toString().trim()
            val cardNumber = layoutBankCardInput.etBankCard.text.toString().trim()
            val expiryDate = layoutBankCardInput.etExpiry.text.toString().trim()
            val cvv = layoutBankCardInput.etCvv.text.toString().trim()
            var remainingBalance = SharedPreferenceHelper().getUser().walletCard?.avilableBalance
            if (isWalletSelected) {
                if ((remainingBalance ?: 0.0) > getTotalPrice()) {
                    remainingBalance = (remainingBalance ?: 0.0) - getTotalPrice()

                } else {
                    view?.rootView?.snackbar(
                        stringId = R.string.insufficiant_amount,
                        anchorViewId = anchorViewId,
                        drawableId = R.drawable.ic_round_error_24,
                        color = NoteColor.Error,
                        vibrate = true
                    )
                    return
                }
            } else {
                if (name.isEmpty() && cardNumber.isEmpty() && expiryDate.isEmpty() && cvv.isEmpty()) {
                    view?.rootView?.snackbar(
                        stringId = R.string.details_missing,
                        anchorViewId = anchorViewId,
                        drawableId = R.drawable.ic_round_error_24,
                        color = NoteColor.Error,
                        vibrate = true
                    )
                    layoutBankCardInput.tilName.error = getString(R.string.empty_name)
                    layoutBankCardInput.tilBankCard.error = getString(R.string.empty_card_number)
                    layoutBankCardInput.tilExpiry.error = getString(R.string.empty_expiry)
                    layoutBankCardInput.tilCvv.error = getString(R.string.empty_cvv)
                    return
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
                        layoutBankCardInput.tilBankCard.error =
                            getString(R.string.empty_card_number)
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
            }
            activity?.hideKeyboard(root)

            activity?.let { DialogsManager.showProgressDialog(it) }

            val bankCard = BankCard()
            bankCard.nameOnCard = name
            bankCard.cardNumber = cardNumber
            bankCard.expiryDate = expiryDate
            bankCard.cvv = cvv

            val user = SharedPreferenceHelper().getUser()

            val walletCard = WalletCard()
            walletCard.nameOnCard = name
            walletCard.avilableBalance = remainingBalance

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
                        if (it.isSuccessful) {
                            SharedPreferenceHelper().saveUser(updateUser)
                            updateParkingSpot()
                        }
                    }
            }
        }
    }

    private fun updateParkingSpot() {
        val updateParkingSpot = ParkingSpot()
        updateParkingSpot.availabilityStatus = ConstantFirebase.AVAILABILITY_STATUS.OCCUPIED.name
        updateParkingSpot.insertedAt = Calendar.getInstance().timeInMillis
        updateParkingSpot.address = parkingSpot?.address
        updateParkingSpot.latitude = parkingSpot?.latitude ?: 0.0
        updateParkingSpot.longitude = parkingSpot?.longitude ?: 0.0
        updateParkingSpot.parkingId = parkingSpot?.parkingId
        updateParkingSpot.pricePerHr = parkingSpot?.pricePerHr ?: 0.0
        withDelay {
            firestore.collection(ConstantFirebase.COLLECTION_PARKING_SPOT)
                .document(parkingSpot?.documentId.toString())
                .set(updateParkingSpot)
                .addOnCompleteListener {
                    DialogsManager.dismissProgressDialog()
                    if (it.isSuccessful) {
                        view?.rootView?.snackbar(
                            stringId = R.string.booking_success,
                            drawableId = R.drawable.ic_round_check_circle_24,
                            anchorViewId = anchorViewId,
                            color = NoteColor.Success,
                        )
                        ::navigateWithDelay.withDelay(ConstantDelay.NAVIGATION_DELAY)
                    } else {
                        view?.rootView?.snackbar(
                            stringId = R.string.booking_failed,
                            drawableId = R.drawable.ic_round_error_24,
                            anchorViewId = anchorViewId,
                            color = NoteColor.Error,
                            vibrate = true
                        )
                    }
                }
                .addOnFailureListener {
                    view?.rootView?.snackbar(
                        stringId = R.string.booking_failed,
                        drawableId = R.drawable.ic_round_error_24,
                        anchorViewId = anchorViewId,
                        color = NoteColor.Error,
                        vibrate = true
                    )
                }
        }
    }

    fun getTotalPrice(): Double {
        return (parkingSpot?.pricePerHr ?: 0.0) * (bookedSpot?.bookedHours ?: 0.0)
    }

    fun navigateWithDelay() {
        dismiss()
        Navigator.toMainActivity(true)
    }

    fun feedDebugData() {
        binding.apply {
            layoutBankCardInput.etName.setText("Dummy Name")
            layoutBankCardInput.etBankCard.setText("1234567890123456")
            layoutBankCardInput.etExpiry.setText("02/25")
            layoutBankCardInput.etCvv.setText("999")
        }
    }

    companion object {
        const val DIALOG_TITLE = "Payment Confirmation"
        const val PARKING_ID = "ID: %s"
        const val BACK_PRESS_DIALOG_TITLE = "Confirmation"
        const val BACK_PRESS_DIALOG_CONFIRMATION = "Are you sure you dot not want to reserve?"
        const val BACK_PRESS_DIALOG_DISCRIPTION =
            "To reserve you need to fill details and pay the asked amount."
        const val BACK_PRESS_DIALOG_POSITIVE_BUTTON = "YES"
        const val BACK_PRESS_DIALOG_NAGATIVE_BUTTON = "NO"
        const val TOTAL_AMOUNT = "Payable Amount : %s"
    }
}