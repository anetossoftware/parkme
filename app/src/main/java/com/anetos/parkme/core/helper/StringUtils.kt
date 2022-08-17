package com.anetos.parkme.core.helper

import android.text.TextUtils

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
}