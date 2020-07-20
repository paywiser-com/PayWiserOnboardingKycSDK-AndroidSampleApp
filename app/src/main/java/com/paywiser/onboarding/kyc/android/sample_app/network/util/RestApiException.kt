package com.paywiser.onboarding.kyc.android.sample_app.network.util

import okhttp3.ResponseBody

class RestApiException: Exception() {

    companion object {
        private const val CUSTOM_MESSAGE =
            "Error calling API method '%s'. HttpStatusCode is %d. Response body is '%s'"


        fun create(methodName: String, httpStatusCode: Int, errorBody: ResponseBody?): Exception {
            return Exception(
                CUSTOM_MESSAGE.format(
                    methodName,
                    httpStatusCode,
                    errorBody.toString()
                )
            )
        }
    }
}