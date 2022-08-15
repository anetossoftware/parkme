package com.anetos.parkme.view.widget.home

import android.content.Context
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
import com.anetos.parkme.data.ConstantFirebase
import com.anetos.parkme.data.model.ParkingSpot
import com.anetos.parkme.data.model.User
import com.anetos.parkme.databinding.FragmentHomeBinding
import com.anetos.parkme.view.widget.about.AboutDialogFragment
import com.anetos.parkme.view.widget.common.ConfirmationDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding

    private val anchorViewId by lazy { R.id.fab }

    val db = Firebase.firestore
    var user = User()
    var bookedParkingSpot = ParkingSpot()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getBookedSpot()
    }

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
        binding.fab.setOnClickListener {
            val uri = String.format(
                URL,
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
                R.id.more -> {

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

    fun calculatePrice(): String {
        return (bookedParkingSpot.pricePerHr * (user.bookedSpot?.bookedHours ?: 0.0)).toString()
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
        /*val uri: String = String.format("geo:%f,%f", latitude, longitude)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        context!!.startActivity(intent)*/
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = geoLocation
        }
        if (context?.packageManager?.let { intent.resolveActivity(it) } != null) {
            startActivity(intent)
        }
    }

    private fun setData() {
        binding.apply {
            valuePrice.text = calculatePrice()
            valueHours.text = user.bookedSpot?.bookedHours.toString()
            valueBookedOn.text = user.bookedSpot?.bookedFrom?.convertLongToTime(SHORT_DATE_FORMAT)
            valueBookedTill.text = user.bookedSpot?.bookedTill?.convertLongToTime(SHORT_DATE_FORMAT)
            tvLabelParkingid.text = String.format(PARKING_ID, bookedParkingSpot.parkingId)
            tvLabelPrice.text = String.format(PARKING_PRICE, bookedParkingSpot.pricePerHr)
            tvLabelParkingAddress.text = String.format(PARKING_ADDRESS, bookedParkingSpot.address)
        }
    }

    fun showProfile() {
        navController?.navigateSafely(HomeFragmentDirections.actionHomeFragmentToProfileFragment(user, bookedParkingSpot))
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
                    Navigator.toLoginActivity()
                }
            }
        }).show(requireActivity().supportFragmentManager, null)
    }

    companion object {
        fun newInstance(ctx: Context) = HomeFragment()
        const val TOTAL_PRICE = "Total Price"
        const val TOTAL_HOURS = "Booked Hours"
        const val BOOKED_ON = "Booked on"
        const val EXPIRES_ON = "Expires on"
        const val URL = "google.navigation:q=%f,%f"
        val TAG = HomeFragment::class.simpleName
        const val PARKING_ID = "ID: %s"
        const val PARKING_PRICE = "Rate: %s CAD / Hr"
        const val PARKING_ADDRESS = "Address: %s"
    }
}