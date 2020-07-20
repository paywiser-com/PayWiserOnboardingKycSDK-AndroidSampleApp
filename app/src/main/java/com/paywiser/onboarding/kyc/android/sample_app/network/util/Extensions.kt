package com.paywiser.onboarding.kyc.android.sample_app.network.util

import retrofit2.Response

fun <T: Any> Response<T>.getResponse(): ResourceResult<T> {
    return try {
        when (this.isSuccessful) {
            true -> ResourceResult.Success(this.body()!!)
            false -> ResourceResult.Failure(
                RestApiException.create(
                    ::Any.name.replace("Response","").replace("Request",""),
                    this.code(),
                    this.errorBody()
                )
            )
        }
    } catch (ex: Exception) {
        return ResourceResult.Failure(ex)
    }
}
