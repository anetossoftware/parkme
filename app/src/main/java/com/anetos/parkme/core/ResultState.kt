package com.anetos.parkme.core

/**
 * Helper class for api data used in retrofit
 *
 * created by Jaydeep Bhayani on 16/07/2022
 */
sealed class ResultState<out T : Any> {

    class Loading(val isLoading: Boolean = true) : ResultState<Nothing>()

    class Success<out T : Any>(val data: T) : ResultState<T>()

    class Error(val exception: Any) : ResultState<Nothing>()
}