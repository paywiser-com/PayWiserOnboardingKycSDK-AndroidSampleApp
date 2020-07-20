package com.paywiser.onboarding.kyc.android.sample_app.network

import com.google.gson.GsonBuilder
import com.paywiser.onboarding.kyc.android.sample_app.model.GetKYCDataRequest
import com.paywiser.onboarding.kyc.android.sample_app.model.GetKYCDataResponse
import com.paywiser.onboarding.kyc.android.sample_app.network.util.BasicAuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.net.URL
import java.util.concurrent.TimeUnit

interface OnboardingKycWhiteLabelApiService {

    @POST("GetKYCData")
    suspend fun getKYCData(@Body body: GetKYCDataRequest): Response<GetKYCDataResponse>

    companion object {

        fun createService(apiUrl: String, username: String, password: String): OnboardingKycWhiteLabelApiService {
            val url = URL(apiUrl)

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                //.writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(
                    BasicAuthInterceptor(
                        username,
                        password
                    )
                )
                .build()

            return Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .client(okHttpClient)
                .build().create(OnboardingKycWhiteLabelApiService::class.java)
        }
    }
}