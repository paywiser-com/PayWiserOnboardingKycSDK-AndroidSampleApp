package com.paywiser.onboarding.kyc.android.sample_app.network.util

sealed class ResourceResult<out T : Any> {

    data class Success<out T : Any>(val response: T) : ResourceResult<T>()
    data class Failure(val exception: Exception) : ResourceResult<Nothing>()
    object NetworkError : ResourceResult<Nothing>()
    data class Loading(val isLoading: Boolean) : ResourceResult<Nothing>()
}