package com.anetos.parkme.view.widget.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.anetos.parkme.R
import com.anetos.parkme.core.helper.*
import com.anetos.parkme.core.helper.StringUtils.formatAccountNumber
import com.anetos.parkme.core.helper.dialog.DialogsManager
import com.anetos.parkme.data.ConstantFirebase
import com.anetos.parkme.data.model.BankCard
import com.anetos.parkme.data.model.BookedSpot
import com.anetos.parkme.data.model.User
import com.anetos.parkme.data.model.WalletCard
import com.anetos.parkme.databinding.FragmentProfileBinding
import com.anetos.parkme.view.widget.wallet.AddMoneyToWalletDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ProfileFragment : Fragment() {

    private val args by navArgs<ProfileFragmentArgs>()
    private val anchorViewId by lazy { R.id.fab }
    private val firestore = FirebaseFirestore.getInstance()

    private var isEditOn = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentProfileBinding.inflate(inflater, container, false).withBinding {
            setupMixedTransitions()
            setupState()
            setupListeners()
        }

    private fun FragmentProfileBinding.setupState() {
        val userData = args.userData

        etMobile.disableLook()
        disableProfile()
        etName.setText(userData?.name)
        etEmail.setText(userData?.emailAddress)
        countryCode.setDefaultCountryUsingNameCode(userData?.countryCode)
        etMobile.setText(userData?.mobileNumber)

        setupBankCard(userData?.bankCard)
        setupWalletCard(userData?.walletCard)
    }

    private fun FragmentProfileBinding.setupListeners() {
        activity?.onBackPressedDispatcher?.addCallback {
            navController?.navigateUp()
        }
        tb.setNavigationOnClickListener {
            navController?.navigateUp()
        }
        tb.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit_profile -> {
                    if (isEditOn) {
                        isEditOn = false
                        editProfile()
                    } else {
                        isEditOn = true
                        disableProfile()
                    }
                }
            }
            false
        }
        val paymentType = tlPaymentLayout.selectedTabPosition.let {
            when (it) {
                0 -> getString(R.string.bank_card)
                else -> getString(R.string.wallet)
            }
        }
        tlPaymentLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                if (getString(R.string.bank_card).equals(tab.text.toString(), true)) {
                    layoutCard.root.visible()
                    layoutWallet.root.gone()
                } else if (getString(R.string.wallet).equals(tab.text.toString(), true)) {
                    layoutWallet.root.visible()
                    layoutCard.root.gone()
                }
            }
        })
        layoutWallet.efabAddMoney.setOnClickListener {
            AddMoneyToWalletDialogFragment().onClickListener(
                object : AddMoneyToWalletDialogFragment.onClickListener {
                    override fun onSuccess(data: Any) {
                        setupBankCard((data as User).bankCard)
                        setupWalletCard(data.walletCard)
                    }
                }
            ).show(requireActivity().supportFragmentManager, null)
        }
        fab.setOnClickListener {
            update()
        }
    }

    fun FragmentProfileBinding.setupBankCard(bankCard: BankCard?) {
        layoutCard.apply {
            tvCardNumber.text = bankCard?.cardNumber?.let { formatAccountNumber(it) }
            tvValueExpiry.text = bankCard?.expiryDate
            tvCardHolderName.text = bankCard?.nameOnCard
        }
    }

    fun FragmentProfileBinding.setupWalletCard(walletCard: WalletCard?) {
        layoutWallet.apply {
            tvValueBalance.text = (formatAmount(Currency.getInstance(Locale.CANADA), walletCard?.avilableBalance ?: 0.0))
        }
    }

    private fun FragmentProfileBinding.editProfile() {
        tb.menu.findItem(R.id.edit_profile).setIcon(R.drawable.ic_round_edit_off_24)
        etName.enableLook()
        etEmail.enableLook()
        etAddress.enableLook()
        fab.visible()
    }

    private fun FragmentProfileBinding.disableProfile() {
        tb.menu.findItem(R.id.edit_profile).setIcon(R.drawable.ic_round_edit_24)
        etName.disableLook()
        etEmail.disableLook()
        etAddress.disableLook()
        fab.gone()
    }

    private fun FragmentProfileBinding.update() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val address = etAddress.text.toString().trim()
        if (name.isEmpty() || email.isEmpty()) {
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
        }
        if (email.indexOf("@") < 0 || email.indexOf(".") < 0) {
            tilName.error = getString(R.string.invalid_email)
            return
        }
        activity?.hideKeyboard(root)
        context?.let { DialogsManager.showProgressDialog(it) }

        val user = SharedPreferenceHelper().getUser()

        val bankCard = BankCard()
        bankCard.nameOnCard = user.bankCard?.nameOnCard
        bankCard.cardNumber = user.bankCard?.cardNumber
        bankCard.expiryDate = user.bankCard?.expiryDate
        bankCard.cvv = user.bankCard?.cvv

        val walletCard = WalletCard()
        walletCard.nameOnCard = user.bankCard?.nameOnCard
        walletCard.avilableBalance = user.walletCard?.avilableBalance

        val bookedSpot = BookedSpot()
        bookedSpot.bookedParkingId = user.bookedSpot?.bookedParkingId.toString()
        bookedSpot.bookedFrom = user.bookedSpot?.bookedFrom
        bookedSpot.bookedTill = user.bookedSpot?.bookedTill
        bookedSpot.bookedHours = user.bookedSpot?.bookedHours

        val updateUser = User()
        updateUser.name = name
        updateUser.countryNameCode = user.countryNameCode.toString()
        updateUser.countryCode = user.countryCode.toString()
        updateUser.mobileNumber = user.mobileNumber.toString()
        updateUser.emailAddress = email
        updateUser.address = address
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
                        view?.rootView?.snackbar(
                            stringId = R.string.success_profile_update,
                            drawableId = R.drawable.ic_round_check_circle_24,
                            anchorViewId = anchorViewId,
                            color = NoteColor.Success,
                        )
                        disableProfile()
                    } else {
                        view?.rootView?.snackbar(
                            stringId = R.string.failure_profile_update,
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

    companion object
}