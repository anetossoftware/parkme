package com.anetos.parkme.core.helper

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

const val SIMPLE_DATE_FORMAT = "yyyy-MM-dd"
const val EXPIRY_PATTERN = "MM/yy"
val SHORT_DATE_FORMAT = "MMM dd, yy HH:mm"

//EEEE, MMM d, yyyy h:mm:ss a
fun Long.convertLongToTime(dateFormat: String? = "MMMM dd, yyyy HH:mm"): String {
    val date = Date(this)
    val format = SimpleDateFormat(dateFormat)
    return format.format(date)
}

@SuppressLint("SimpleDateFormat")
fun String.convertDateTimeToLong(): Long {
    val df = SimpleDateFormat("MMMM dd, yyyy HH:mm")
    return df.parse(this)?.time ?: 0
}

@SuppressLint("SimpleDateFormat")
fun String.expiryDate(): Boolean {
    try {
        val simpleDateFormat = SimpleDateFormat(EXPIRY_PATTERN)
        simpleDateFormat.isLenient = false
        val expiry = simpleDateFormat.parse(this)
        return expiry?.before(Date()) ?: false
    } catch (e: Exception) {
        return true
    }
}

fun validateCardExpiryDate(expiryDate: String): Boolean {
    return expiryDate.matches("(?:0[1-9]|1[0-2])/[0-9]{2}".toRegex())
}