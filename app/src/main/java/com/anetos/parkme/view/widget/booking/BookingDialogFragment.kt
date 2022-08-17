package com.anetos.parkme.view.widget.booking

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anetos.parkme.R
import com.anetos.parkme.core.helper.*
import com.anetos.parkme.data.model.BookedSpot
import com.anetos.parkme.data.model.ParkingSpot
import com.anetos.parkme.databinding.DialogFragmentBookingBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.util.*
import java.util.concurrent.TimeUnit

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
        }
    }

    private fun setupState() {
        binding.apply {
            tvLabelParkingid.text = String.format(PARKING_ID, parkingSpot?.parkingId)
            tvLabelPrice.text = String.format(
                PARKING_PRICE,
                formatAmount(
                    Currency.getInstance(Locale.CANADA),
                    parkingSpot?.pricePerHr ?: 0.0
                ).plus("/hr")
            )
            tvLabelParkingAddress.text = String.format(PARKING_ADDRESS, parkingSpot?.address)
        }
    }

    private fun setupListeners() {
        binding.apply {
            tilFromTime.setEndIconOnClickListener {
                showDateTimeDialog(SELECT_DATE_FROM)
            }
            tilToTime.setEndIconOnClickListener {
                showDateTimeDialog(SELECT_DATE_TO)
            }
            btnConfirm.setOnClickListener {
                bookParking()
            }
        }
    }

    private fun bookParking() {
        binding.apply {
            val fromTime = etFromTime.text.toString().trim()
            val toTime = etToTime.text.toString().trim()
            if (fromTime.isEmpty() && toTime.isEmpty()) {
                view?.rootView?.snackbar(
                    stringId = R.string.details_missing,
                    anchorViewId = anchorViewId,
                    drawableId = R.drawable.ic_round_error_24,
                    color = NoteColor.Error,
                    vibrate = true
                )
                tilFromTime.error = getString(R.string.empty_from_time)
                tilToTime.error = getString(R.string.empty_to_time)
                return
            }
            if (fromTime.isEmpty()) {
                tilFromTime.isErrorEnabled = true
                context?.let {
                    tilFromTime.error = getString(R.string.empty_from_time)
                }
                withDelay(2000L) { tilFromTime.isErrorEnabled = false }
                return
            } else {
                tilFromTime.isErrorEnabled = false
            }
            if (toTime.isEmpty()) {
                tilToTime.isErrorEnabled = true
                context?.let {
                    tilToTime.error = getString(R.string.empty_to_time)
                }
                withDelay(2000L) { tilToTime.isErrorEnabled = false }
                return
            } else {
                tilToTime.isErrorEnabled = false
            }
            if (calculateHours() <= 0.0) {
                tilToTime.error = getString(R.string.to_time_error)
                withDelay(2000L) { tilToTime.isErrorEnabled = false }
                return
            } else if (calculateHours() < 1) {
                tilToTime.error = getString(R.string.one_hour_error)
                withDelay(2000L) { tilToTime.isErrorEnabled = false }
                return
            } else {
                tilToTime.isErrorEnabled = false
            }
            activity?.hideKeyboard(root)

            val bookedSpot = BookedSpot()
            bookedSpot.bookedParkingId = parkingSpot?.parkingId.toString()
            bookedSpot.bookedFrom = fromTime.convertDateTimeToLong()
            bookedSpot.bookedTill = toTime.convertDateTimeToLong()
            bookedSpot.bookedHours = calculateHours()

            activity?.supportFragmentManager?.let {
                BookingPaymentDialogFragment(parkingSpot, bookedSpot).show(it, null)
            }
        }
    }

    private fun showDateTimeDialog(value: String) {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        val isDarkMode =
            resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        val theme = if (isDarkMode) android.R.style.Theme_DeviceDefault_Dialog
        else android.R.style.Theme_DeviceDefault_Light_Dialog

        val is24HourFormat = DateFormat.is24HourFormat(context)

        context?.let { context ->
            val buttonTextColor = context.colorAttributeResource(R.attr.notePrimaryColor)
            DatePickerDialog(context, theme, { _, year, month, day ->
                TimePickerDialog(context, theme, { _, hour, minute ->
                    LocalDateTime(year, month + 1, day, hour, minute)
                        .toInstant(TimeZone.currentSystemDefault())
                        .also {
                            //println("Current"+it.toString())
                            //binding.root.rootView.snackbar(it.toString())
                        }
                        .toEpochMilliseconds()
                        .also {
                            setDate(value, it)
                        }
                }, startHour, startMinute, is24HourFormat)
                    .apply {
                        show()
                        getButton(DatePickerDialog.BUTTON_POSITIVE)?.setTextColor(buttonTextColor)
                        getButton(DatePickerDialog.BUTTON_NEUTRAL)?.setTextColor(buttonTextColor)
                        getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setTextColor(buttonTextColor)
                    }
            }, startYear, startMonth, startDay)
                .apply {
                    datePicker.minDate = Clock.System
                        .now()
                        .toEpochMilliseconds()
                        .minus(1000)
                    show()
                    getButton(DatePickerDialog.BUTTON_POSITIVE)?.setTextColor(buttonTextColor)
                    getButton(DatePickerDialog.BUTTON_NEUTRAL)?.setTextColor(buttonTextColor)
                    getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setTextColor(buttonTextColor)
                }
        }
    }

    private fun setDate(value: String, time: Long) {
        if (value.equals(SELECT_DATE_FROM, true))
            binding.etFromTime.setText(time.convertLongToTime())
        else if (value.equals(SELECT_DATE_TO, true))
            binding.etToTime.setText(time.convertLongToTime())
    }

    private fun calculateHours(): Double {
        val difference = binding.etToTime.text.toString()
            .convertDateTimeToLong() - binding.etFromTime.text.toString().convertDateTimeToLong()
        val hours =
            String.format(Locale.US, TIME_LEFT_FORMAT, TimeUnit.MILLISECONDS.toHours(difference))
        val minutes =  String.format(Locale.US, TIME_LEFT_FORMAT, TimeUnit.MILLISECONDS.toMinutes(difference) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(difference)))
        return (hours.toDouble() + (minutes.toDouble()/60))
    }

    companion object {
        const val DIALOG_TITLE = "Book here!"
        const val PARKING_ID = "ID: %s"
        const val PARKING_PRICE = "Rate: %s"
        const val PARKING_ADDRESS = "Address: %s"
        const val SELECT_DATE_FROM = "FROM"
        const val SELECT_DATE_TO = "TO"
        const val TIME_LEFT_FORMAT = "%02d"
    }
}