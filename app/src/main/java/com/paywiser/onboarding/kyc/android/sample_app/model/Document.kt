package com.paywiser.onboarding.kyc.android.sample_app.model

data class Document(
    val Classification: String?,
    val Type: String?,
    val Issuer: String?,
    val ExpiryDate: String?,
    val PassportNumber: String?,
    val DocumentNumber: String?,
    val Subject: Subject?
)