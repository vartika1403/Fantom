package com.entertainment.fraternity.data

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val error: String = "", val emailError: Boolean = false) : Resource<Nothing>()
}
