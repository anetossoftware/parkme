package com.anetos.parkme.core

import com.anetos.parkme.core.UiState.Loading
import com.anetos.parkme.core.UiState.Success

sealed interface UiState<out T> {
    //object Loading : UiState<Nothing>
    class Loading<T>(val isLoading: Boolean? = false) : UiState<T>
    data class Success<T>(val value: T) : UiState<T>
}

inline fun <T, R> UiState<T>.map(transform: (value: T) -> R): UiState<R> = when (this) {
    is Loading -> Loading(isLoading)
    is Success -> Success(transform(value))
}

fun <T> UiState<T>.getOrDefault(defaultValue: T) = when (this) {
    is Loading -> defaultValue
    is Success -> value
}