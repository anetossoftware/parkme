package com.anetos.parkme.core.error

import com.google.gson.annotations.SerializedName

data class AppError(
        @SerializedName("errorType")
        val errorType: ErrorType,
        @SerializedName("title")
        val title: String,
        @SerializedName("description")
        val description: String)

enum class ErrorType {
    WARNING,
    NO_CONNECTIVITY
}
