package com.anetos.parkme.core.helper

import com.anetos.parkme.data.model.User

object DataHelper {
    fun getUserIndex(user: User): String {
        return getUserIndex(user.countryCode.toString(), user.mobileNumber.toString())
    }

    fun getUserIndex(code: String, mobile: String): String {
        var index = "$code$mobile"
        if (!index.startsWith("+"))
            index = "+$index"
        return index
    }
}