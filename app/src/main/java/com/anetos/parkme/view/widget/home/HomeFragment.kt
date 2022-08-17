package com.anetos.parkme.view.widget.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import com.anetos.parkme.R
import com.anetos.parkme.core.BaseFragment
import com.anetos.parkme.core.helper.*
import com.anetos.parkme.core.helper.dialog.DialogsManager
import com.anetos.parkme.data.ConstantDelay
import com.anetos.parkme.data.ConstantFirebase
import com.anetos.parkme.data.model.*
import com.anetos.parkme.databinding.FragmentHomeBinding
import com.anetos.parkme.view.widget.about.AboutDialogFragment
import com.anetos.parkme.view.widget.common.ConfirmationDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding

    private val anchorViewId by lazy { R.id.fab }

    val db = Firebase.firestore
    var user = User()
    var bookedParkingSpot = ParkingSpot()
    var isCancelByUser = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupMixedTransitions()
        setupState()
        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        binding.bottomAppBar.setOnSwipeGestureListener {

        }
        binding.bottomAppBar.setNavigationOnClickListener {
            showAbout()
        }
        binding.efabCancelBooking.setOnClickListener {
            isCancelByUser = true
            updateBooking()
        }
        binding.fab.setOnClickListener {
            val uri = String.format(
                MAP_URL,
                bookedParkingSpot.latitude,
                bookedParkingSpot.longitude
            )
            showMap(Uri.parse(uri))
        }
        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profile -> {
                    setupFadeTransition()
                    showProfile()
                    true
                }
                else -> false
            }
        }
        binding.tb.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    logout()
                }
            }
            false
        }
    }

    private fun setupState() {
        getBookedSpot()
        binding.apply {
            bottomAppBar.setRoundedCorners()
            activity?.let { context ->
                val backgroundColor = context.colorAttributeResource(R.attr.noteBackgroundColor)
                binding.bottomAppBar.navigationIcon?.mutate()?.setTint(backgroundColor)
                binding.bottomAppBar.menu.forEach { it.icon?.mutate()?.setTint(backgroundColor) }
            }
            labelPrice.text = TOTAL_PRICE
            labelHours.text = TOTAL_HOURS
            labelBookedOn.text = BOOKED_ON
            labelBookedTill.text = EXPIRES_ON
        }

    }

    fun calculatePrice(): Double {
        return (bookedParkingSpot.pricePerHr * (user.bookedSpot?.bookedHours ?: 0.0))
    }

    fun getBookedSpot() {
        user = SharedPreferenceHelper().getUser()
        db.collection(ConstantFirebase.COLLECTION_PARKING_SPOT)
            .get()
            .addOnSuccessListener { result ->
                val parkingSpotList: MutableList<ParkingSpot> = ArrayList()
                try {
                    for (document in result) {
                        val data = document.data
                        val parkingSpot = ParkingSpot()
                        parkingSpot.documentId = document.id
                        parkingSpot.parkingId = data.get(ParkingSpot::parkingId.name).toString()
                        parkingSpot.address = data.get(ParkingSpot::address.name).toString()
                        parkingSpot.latitude = data.get(ParkingSpot::latitude.name) as Double
                        parkingSpot.longitude = data.get(ParkingSpot::longitude.name) as Double
                        parkingSpot.pricePerHr = data.get(ParkingSpot::pricePerHr.name) as Double
                        parkingSpot.availabilityStatus =
                            data.get(ParkingSpot::availabilityStatus.name).toString()
                        Log.d(TAG, "${document.id} => ${document.data}")
                        if (user.bookedSpot?.bookedParkingId.equals(parkingSpot.parkingId)) {
                            bookedParkingSpot = parkingSpot
                            break
                        }
                        parkingSpotList.add(parkingSpot)
                    }
                    setData()
                } catch (e: Exception) {
                    Log.e(TAG, e.message.toString())
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }
    }

    fun showMap(geoLocation: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = geoLocation
        }
        if (context?.packageManager?.let { intent.resolveActivity(it) } != null) {
            startActivity(intent)
        }
    }

    private fun setData() {
        binding.apply {
            valuePrice.text = formatAmount(Currency.getInstance(Locale.CANADA), calculatePrice())
            valueHours.text = user.bookedSpot?.bookedHours.toString()
            valueBookedOn.text = user.bookedSpot?.bookedFrom?.convertLongToTime(SHORT_DATE_FORMAT)
            valueBookedTill.text = user.bookedSpot?.bookedTill?.convertLongToTime(SHORT_DATE_FORMAT)
            tvLabelParkingid.text = String.format(PARKING_ID, bookedParkingSpot.parkingId)
            tvLabelPrice.text = String.format(PARKING_PRICE, bookedParkingSpot.pricePerHr)
            tvLabelParkingAddress.text = String.format(PARKING_ADDRESS, bookedParkingSpot.address)
        }
        if ((user.bookedSpot?.bookedTill ?: 0) < Calendar.getInstance().timeInMillis) {
            updateBooking()
        }
    }

    fun showProfile() {
        navController?.navigateSafely(
            HomeFragmentDirections.actionHomeFragmentToProfileFragment(
                user,
                bookedParkingSpot
            )
        )
    }

    private fun showAbout() {
        activity?.supportFragmentManager?.let {
            AboutDialogFragment().show(it, null)
        }
    }

    fun logout() {
        ConfirmationDialogFragment(
            dialogTitle = "Logout",
            confirmation = "Logout confirmation",
            description = "Are you sure you want to logout from application?",
            buttonText = "Logout",
        ).onClickListener(object : ConfirmationDialogFragment.onConfirmationClickListener {
            override fun onClick(confirmationDialogFragment: ConfirmationDialogFragment) {
                context?.let {
                    SharedPreferenceHelper().clearAppPreferences()
                    FirebaseAuth.getInstance().signOut()
                    binding.root.snackbar(
                        stringId = R.string.logout_successful,
                        drawableId = R.drawable.ic_round_check_circle_24,
                        anchorViewId = anchorViewId,
                        color = NoteColor.Success,
                        vibrate = true
                    )
                    confirmationDialogFragment.dismiss()
                }
                withDelay(1000) {
                    activity?.finish()
                    Navigator.toLoginActivity(true)
                }
            }
        }).show(requireActivity().supportFragmentManager, null)
    }

    private fun updateUser() {
        context?.let { DialogsManager.showProgressDialog(it) }
        val bankCard = BankCard()
        bankCard.nameOnCard = user.bankCard?.nameOnCard
        bankCard.cardNumber = user.bankCard?.cardNumber
        bankCard.expiryDate = user.bankCard?.expiryDate
        bankCard.cvv = user.bankCard?.cvv

        val walletCard = WalletCard()
        walletCard.nameOnCard = user.walletCard?.nameOnCard
        walletCard.avilableBalance = user.walletCard?.avilableBalance

        val bookedSpot = BookedSpot()
        bookedSpot.bookedParkingId = null
        bookedSpot.bookedFrom = null
        bookedSpot.bookedTill = null
        bookedSpot.bookedHours = null

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
            db.collection(ConstantFirebase.COLLECTION_USERS)
                .document(DataHelper.getUserIndex(SharedPreferenceHelper().getUser()))
                .set(updateUser)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        SharedPreferenceHelper().saveUser(updateUser)
                        cancelParkingSpot()
                    }
                }
        }
    }

    private fun cancelParkingSpot() {
        val updateParkingSpot = ParkingSpot()
        updateParkingSpot.availabilityStatus = ConstantFirebase.AVAILABILITY_STATUS.AVAILABLE.name
        updateParkingSpot.insertedAt = Calendar.getInstance().timeInMillis
        updateParkingSpot.address = bookedParkingSpot.address
        updateParkingSpot.latitude = bookedParkingSpot.latitude
        updateParkingSpot.longitude = bookedParkingSpot.longitude
        updateParkingSpot.parkingId = bookedParkingSpot.parkingId
        updateParkingSpot.pricePerHr = bookedParkingSpot.pricePerHr
        withDelay {
            db.collection(ConstantFirebase.COLLECTION_PARKING_SPOT)
                .document(bookedParkingSpot.documentId.toString())
                .set(updateParkingSpot)
                .addOnCompleteListener {
                    DialogsManager.dismissProgressDialog()
                    if (it.isSuccessful) {
                        view?.rootView?.snackbar(
                            stringId = R.string.booking_cancel_success,
                            drawableId = R.drawable.ic_round_check_circle_24,
                            anchorViewId = anchorViewId,
                            color = NoteColor.Success,
                        )
                        ::navigateWithDelay.withDelay(ConstantDelay.NAVIGATION_DELAY)
                    } else {
                        view?.rootView?.snackbar(
                            stringId = R.string.booking_cancel_failed,
                            drawableId = R.drawable.ic_round_error_24,
                            anchorViewId = anchorViewId,
                            color = NoteColor.Error,
                            vibrate = true
                        )
                    }
                }
                .addOnFailureListener {
                    view?.rootView?.snackbar(
                        stringId = R.string.booking_cancel_failed,
                        drawableId = R.drawable.ic_round_error_24,
                        anchorViewId = anchorViewId,
                        color = NoteColor.Error,
                        vibrate = true
                    )
                }
        }
    }

    fun updateBooking() {
        if (isCancelByUser) {
            ConfirmationDialogFragment(
                dialogTitle = "Cancellation",
                confirmation = "Cancel confirmation",
                description = "Are you sure you want to cancel your parking spot? If yes, you will not get any refund amount.",
                buttonText = "Confirm",
            ).onClickListener(object : ConfirmationDialogFragment.onConfirmationClickListener {
                override fun onClick(confirmationDialogFragment: ConfirmationDialogFragment) {
                    updateUser()
                }
            }).show(requireActivity().supportFragmentManager, null)
        } else {
            ConfirmationDialogFragment(
                dialogTitle = "Alert",
                confirmation = "Booked Timeover",
                description = "Time of your booked spot is over, please click okay to move ahead",
                buttonText = "Okay!",
                isCancelable = false
            ).onClickListener(object : ConfirmationDialogFragment.onConfirmationClickListener {
                override fun onClick(confirmationDialogFragment: ConfirmationDialogFragment) {
                    updateUser()
                }
            }).show(requireActivity().supportFragmentManager, null)
        }

    }

    fun navigateWithDelay() {
        Navigator.toMainActivity(true)
    }

    companion object {
        const val TOTAL_PRICE = "Total Price"
        const val TOTAL_HOURS = "Booked Hours"
        const val BOOKED_ON = "Booked on : "
        const val EXPIRES_ON = "Expires on : "
        const val MAP_URL = "google.navigation:q=%f,%f"
        val TAG = HomeFragment::class.simpleName
        const val PARKING_ID = "ID : %s"
        const val PARKING_PRICE = "Rate : %s CAD / Hr"
        const val PARKING_ADDRESS = "Address : %s"
    }
}