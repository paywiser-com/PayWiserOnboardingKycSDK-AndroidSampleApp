package com.paywiser.onboarding.kyc.android.sample_app.model

data class GetKYCDataRequest(
    val ReferenceID: String,
    val KYCID: String
)

data class GetKYCDataResponse(
    val ReferenceID: String?,
    val CallerReferenceI: String?,
    val Process: Process?,
    val Document: Document?,
    val PersonalData: PersonalData?,
    val StatusCode: Int,
    val StatusDescription: String
)