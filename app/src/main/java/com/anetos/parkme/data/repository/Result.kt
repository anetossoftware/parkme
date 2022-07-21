package com.anetos.parkme.data.repository

/**
 * Helper class for api data used in retrofit
 *
 * created by Jaydeep Bhayani on 16/07/2022
 */
sealed class Result<out T : Any> {

    class Success<out T : Any>(val data: T) : Result<T>()

    class Error(val exception: Throwable) : Result<Nothing>()
}