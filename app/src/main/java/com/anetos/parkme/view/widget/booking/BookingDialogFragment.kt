package com.anetos.parkme.view.widget.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anetos.parkme.Application
import com.anetos.parkme.BuildConfig
import com.anetos.parkme.R
import com.anetos.parkme.core.helper.*
import com.anetos.parkme.data.ConstantFirebase
import com.anetos.parkme.data.model.BankCard
import com.anetos.parkme.data.model.ParkingSpot
import com.anetos.parkme.data.model.User
import com.anetos.parkme.databinding.DialogFragmentBookingBinding
import com.anetos.parkme.view.widget.common.BackPressDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class BookingDialogFragment(
    var parkingSpot: ParkingSpot? = null,
) : BaseDialogFragment() {
    private lateinit var binding: DialogFragmentBookingBinding

    private val firestore = FirebaseFirestore.getInstance()

    private val anchorViewId by lazy { R.id.btn_confirm }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isCancelable = false
        binding = DialogFragmentBookingBinding.inflate(inflater, container, false)
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
            tvLabelParkingid.text = String.format(PARKING_ID, parkingSpot?.parkingId)
            tvLabelPrice.text = String.format(PARKING_PRICE, parkingSpot?.pricePerHr)
            tvLabelParkingAddress.text = String.format(PARKING_ADDRESS, parkingSpot?.address)
        }
    }

    private fun setupListeners() {
        binding.apply {
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
                            this@BookingDialogFragment.dismiss()
                        }
                    }).show(it, null)
                }
            }
        }
    }

    private fun bookParking() {
        binding.apply {
            val name = etName.text.toString().trim()
            val cardNumber = etBankCard.text.toString().trim()
            val expiryDate = etExpiry.text.toString().trim()
            val cvv = etCvv.text.toString().trim()
            if (name.isEmpty() && cardNumber.isEmpty() && expiryDate.isEmpty() && cvv.isEmpty()) {
                view?.rootView?.snackbar(
                    stringId = R.string.details_missing,
                    anchorViewId = anchorViewId,
                    drawableId = R.drawable.ic_round_error_24,
                    color = NoteColor.DarkRed,
                    vibrate = true
                )
                tilName.error = getString(R.string.empty_name)
                tilBankCard.error = getString(R.string.empty_card_number)
                tilExpiry.error = getString(R.string.empty_expiry)
                tilCvv.error = getString(R.string.empty_cvv)
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
            if (cardNumber.length < 16) {
                tilBankCard.isErrorEnabled = true
                context?.let {
                    tilBankCard.error = getString(R.string.empty_card_number)
                }
                return
            } else {
                tilBankCard.isErrorEnabled = false
            }
            if (expiryDate.expiryDate()) {
                tilExpiry.error = getString(R.string.empty_expiry)
                return
            } else {
                tilExpiry.isErrorEnabled = false
            }
            if (cvv.length < 3) {
                tilCvv.error = getString(R.string.empty_cvv)
                return
            } else {
                tilCvv.isErrorEnabled = false
            }
            activity?.hideKeyboard(root)
            onClick?.onClick(this@BookingDialogFragment)
            val bankCard = BankCard()
            bankCard.nameOnCard = name
            bankCard.cardNumber = cardNumber
            bankCard.expiryDate = expiryDate
            bankCard.cvv = cvv
            val user = SharedPreferenceHelper().getUser()
            val updateUser = User(
                user.name.toString(),
                user.countryNameCode.toString(),
                user.countryCode.toString(),
                user.mobileNumber.toString(),
                user.emailAddress.toString(),
                user.address.toString(),
                user.role.toString(),
                bankCard,
                user.userSubscribe.toString(),
                user.service.toString(),
                parkingSpot?.parkingId.toString()
            )
            withDelay {
                firestore.collection(ConstantFirebase.COLLECTION_USERS)
                    .document(DataHelper.getUserIndex(SharedPreferenceHelper().getUser()))
                    .set(updateUser)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            SharedPreferenceHelper().saveUser(updateUser)
                            updateParkingSpot()
                        } else {
                            onClick?.onFailure(this@BookingDialogFragment)
                        }
                    }
                    .addOnFailureListener {
                        onClick?.onFailure(this@BookingDialogFragment)
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
                    if (it.isSuccessful) {
                        dismiss()
                        onClick?.onNavigationClick(this@BookingDialogFragment)
                    } else {
                        onClick?.onFailure(this@BookingDialogFragment)
                    }
                }
                .addOnFailureListener {
                    onClick?.onFailure(this@BookingDialogFragment)
                }
        }
    }

    var onClick: onClickListener? = null

    interface onClickListener {
        fun onClick(bookingDialogFragment: BookingDialogFragment)
        fun onFailure(bookingDialogFragment: BookingDialogFragment)
        fun onNavigationClick(bookingDialogFragment: BookingDialogFragment)
    }

    fun onClickListener(onClick: onClickListener): BookingDialogFragment {
        this.onClick = onClick
        return this
    }

    fun feedDebugData() {
        binding.apply {
            etName.setText("Dummy Name")
            etBankCard.setText("1234567890123456")
            etExpiry.setText("02/25")
            etCvv.setText("999")
        }
    }

    companion object {
        const val DIALOG_TITLE = "Book here!"
        const val PARKING_ID = "ID: %s"
        const val PARKING_PRICE = "Rate: %s CAD / Hr"
        const val PARKING_ADDRESS = "Address: %s"
        const val BACK_PRESS_DIALOG_TITLE = "Confirmation"
        const val BACK_PRESS_DIALOG_CONFIRMATION = "Are you sure you dot not want to reserve?"
        const val BACK_PRESS_DIALOG_DISCRIPTION =
            "To reserve you need to fill and submit the details"
        const val BACK_PRESS_DIALOG_POSITIVE_BUTTON = "YES"
        const val BACK_PRESS_DIALOG_NAGATIVE_BUTTON = "NO"
    }
}