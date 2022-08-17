package com.anetos.parkme.core.helper

import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*

object StringUtils {
    @JvmStatic
    fun join(separator: String, vararg strings: String): String {
        return TextUtils.join(separator, strings)
    }

    @JvmStatic
    fun chunk(string: String, size: Int): Array<String> {
        return string.chunked(size).toTypedArray()
    }

    @JvmStatic
    fun formatAccountNumber(accountNumber: String): String {
        val chunks = chunk(accountNumber, 4)
        return join("  ", *chunks)
    }

    @JvmStatic
    fun removeExtraSpace(strings: String): String {
        return strings.replace(" ", "")
    }

    fun convertIntoCamelCase(string: String): String {
        val chars: CharArray = string.toLowerCase().toCharArray()
        chars[0] = Character.toUpperCase(chars[0])
        return String(chars)
    }

    /**
     * Convert String to Date
     * @param format Date format of string ex:yyyy-MM-dd
     * @return Date
     */
    @JvmStatic
    fun String.toDate(format: String): Date? {
        if (this.isBlank()) return null
        val simpleDateFormat = SimpleDateFormat(format, Locale.US)
        return simpleDateFormat.parse(this)
    }

    /**
     * Change Date String for mate
     * @param inFormat Date format of input string ex:yyyy-MM-dd
     * @param outputFormat Date format of output string ex:dd-MM-yy
     * @return Reformatted String
     */
    /*@JvmStatic
    fun String.toFormattedDateString(inFormat: String, outputFormat: String): String {
        return this.toDate(inFormat)?.toString(outputFormat) ?: ""
    }*/
}