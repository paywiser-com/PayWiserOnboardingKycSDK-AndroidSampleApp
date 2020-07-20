package com.paywiser.onboarding.kyc.android.sample_app.network.util

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.Response


class BasicAuthInterceptor(username: String, password: String): Interceptor {

    private val basicAuthorization = "Basic %s".format(Base64.encodeToString("%s:%s".format(username, password).toByteArray(Charsets.UTF_8), Base64.NO_WRAP))

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request.newBuilder()
            .header("Content-Type", "application/json")
            .header("Authorization", basicAuthorization)

        return chain.proceed(authenticatedRequest.build())
    }

}