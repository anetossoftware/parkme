package com.anetos.parkme.core.helper

import android.annotation.SuppressLint
import androidx.core.util.Preconditions
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


//const val DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
const val DATE_PATTERN = "dd-MMM"
const val TIME_PATTERN = "HH:mm"
const val API_DATE_PATTERN = "MM-dd-yyyy"
const val API_TIME_PATTERN = "HH:mm:ssz"
const val DATE_PATTERN_2 = "yyyy-MM-dd"
const val DATE_PATTERN_ORDINAL = "MMM"
const val SIMPLE_DATE_FORMAT = "yyyy-MM-dd"
const val ISO_DATE = "yyyy-MM-dd'T'HH:mm:ss.sss'Z'"
const val ISO_DATE2 = "yyyy-MM-dd'T'HH:mm:ss"
const val ISO_DATE3 = "yyyy-MM-dd'T'HH:mm"
//const val FORECAST_RECORD_TIME_PATTERN = "ddMMMyyyy HHmm z"
const val FORECAST_RECORD_DATE_PATTERN = "ddMMM"

val API_TIME_FORMAT = SimpleDateFormat(API_TIME_PATTERN, Locale.ENGLISH)
val API_DATE_FORMAT = SimpleDateFormat(API_DATE_PATTERN, Locale.ENGLISH)
val DATE_FORMAT_2 = SimpleDateFormat(DATE_PATTERN_2, Locale.ENGLISH)
val DATE_FORMAT_ORDINAL = SimpleDateFormat(DATE_PATTERN_ORDINAL, Locale.ENGLISH)

//EEEE, MMM d, yyyy h:mm:ss a
fun Long.convertLongToTime(): String {
    val date = Date(this)
    val format = SimpleDateFormat("MMMM dd, yyyy HH:mm")
    return format.format(date)
}

fun String.currentTimeToLong(): Long {
    return System.currentTimeMillis()
}

fun String.convertDateToLong(date: String): Long {
    val df = SimpleDateFormat("yyyy.MM.dd HH:mm")
    return df.parse(date).time
}


val CARD_BENEFIT_FORMAT = "MMM dd, yyyy"

fun String.getISO2StringToCardBenefitDate(): String? {
    val date = parseDate(this, ISO_DATE2)
    val dateFormat: DateFormat = SimpleDateFormat(CARD_BENEFIT_FORMAT, Locale.ENGLISH)
    return dateFormat.format(date)
}

fun String.getSimpleDateToCardBenefitDate(): String? {
    val date = parseDate(this, SIMPLE_DATE_FORMAT)
    val dateFormat: DateFormat = SimpleDateFormat(CARD_BENEFIT_FORMAT, Locale.ENGLISH)
    return dateFormat.format(date)
}

fun parseDate(dateString: String?, format: String?): Date? {
    return if (dateString == null) null else try {
        val dateFormat: DateFormat = SimpleDateFormat(format, Locale.ROOT)
        dateFormat.timeZone = TimeZone.getDefault()
        dateFormat.parse(dateString)
    } catch (e: ParseException) {
        e.printStackTrace()
        null
    }
}


fun String.getForecastDate(): String? {
    val apiFormat = SimpleDateFormat(FORECAST_RECORD_DATE_PATTERN, Locale.ENGLISH)
    val convertedFormat = SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH)
    val date = apiFormat.parse(this)
    return try {
        convertedFormat.format(date!!)
    } catch (e: ParseException) {
        null
    }
}

fun String.getApiTime(): Date? {
    API_TIME_FORMAT.timeZone = TimeZone.getDefault()
    return try {
        API_TIME_FORMAT.parse(this)
    } catch (e: ParseException) {
        null
    }
}

fun yesterday(): Date {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DATE, -1)
    return cal.time
}

fun String.after8Hours(): String? {
    return try {
        val simpleDateFormat = SimpleDateFormat("dd-MMM HH:mm", Locale.ENGLISH)
        val cal = Calendar.getInstance()
        cal.time = simpleDateFormat.parse(this)!!
        cal.add(Calendar.HOUR, 12)
        simpleDateFormat.format(cal.time)

    } catch (e: Exception) {
        null
    }
}

fun String.before8Hours(): String? {
    return try {
        val simpleDateFormat = SimpleDateFormat("dd-MMM HH:mm", Locale.ENGLISH)
        val cal = Calendar.getInstance()
        cal.time = simpleDateFormat.parse(this)!!
        cal.add(Calendar.HOUR, -12)
        simpleDateFormat.format(cal.time)

    } catch (e: Exception) {
        null
    }
}

fun Date.getApiStringDate(pattern: String): String {
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("GMT")
    return dateFormat.format(this)
}

fun Date.getApiStringDate(): String {
    val dateFormat = SimpleDateFormat(API_DATE_PATTERN, Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("GMT")
    return dateFormat.format(this)
}

fun String.getLastMin(): String? {
    val cal = getRoundOffTime()
    cal.time = this.getApiTime()!!
    cal.add(Calendar.MINUTE, -15)
    return API_TIME_FORMAT.format(cal.time).replace("GMT", "")
}

fun String.getTimeFormatMin(): String? {
    val cal = getRoundOffTime()
    cal.time = this.getApiTime()!!
    return TIME_PATTERN.format(cal.time)
}

fun String.getLastHour(): String? {
    val cal = getRoundOffTime()
    cal.time = this.getApiTime()!!
    cal.add(Calendar.HOUR, -1)
    return API_TIME_FORMAT.format(cal.time).replace("GMT", "")
}

fun String.getLastDay(): String? {
    val cal = getRoundOffTime()
    cal.time = this.getApiTime()!!
    cal.add(Calendar.HOUR, -24)
    return API_TIME_FORMAT.format(cal.time).replace("GMT", "")
}

private fun getRoundOffTime(): Calendar {
    val now = Calendar.getInstance()
    now.set(Calendar.SECOND, 0)
    val mod = now.get(Calendar.MINUTE) % 15
    now.add(Calendar.MINUTE, if (mod < 8) -mod else 15 - mod)
    return now
}

fun String.get12HourFormat(): String? {
    // SimpleDateFormat code12Hours = new SimpleDateFormat("HH:mm"); // 24 hour format

    val code12Hours = SimpleDateFormat("hh:mm") // 12 hour format
    val dateCode12 = code12Hours.parse(this)
    var formatTwelve = code12Hours.format(dateCode12) // 12
    if (formatTwelve.equals(this)) {
        formatTwelve = formatTwelve + "AM"
    } else {
        formatTwelve = formatTwelve + "PM"
    }

    return formatTwelve
}

/*
* this function gets the ordinal day's number like, 1st, 12th
* */
@SuppressLint("RestrictedApi")
fun getDayOfMonthSuffix(day: Int): String {
    Preconditions.checkArgument(day >= 1 && day <= 31, "illegal day of month: $day")
    if (day >= 11 && day <= 13) {
        return "th"
    }
    when (day % 10) {
        1 -> return "st"
        2 -> return "nd"
        3 -> return "rd"
        else -> return "th"
    }
}

@SuppressLint("RestrictedApi")
fun getDayOfMonthSuffix(date: String): String {
    val day = date.split("-")[2].toInt()

    val currentTime = DATE_FORMAT_ORDINAL.format(DATE_FORMAT_2.parse(date)!!)

    Preconditions.checkArgument(day >= 1 && day <= 31, "illegal day of month: $day")
    if (day >= 11 && day <= 13) {
        return day.toString() + "th " + currentTime
    }
    when (day % 10) {
        1 -> return day.toString() + "st " + currentTime
        2 -> return day.toString() + "nd " + currentTime
        3 -> return day.toString() + "rd " + currentTime
        else -> return day.toString() + "th " + currentTime
    }
}
