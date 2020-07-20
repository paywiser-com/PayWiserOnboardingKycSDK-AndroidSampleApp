package com.paywiser.onboarding.kyc.android.sample_app

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceManager
import com.paywiser.onboarding.kyc.android.sample_app.model.GetKYCDataRequest
import com.paywiser.onboarding.kyc.android.sample_app.model.GetKYCDataResponse
import com.paywiser.onboarding.kyc.android.sample_app.network.OnboardingKycWhiteLabelApiService
import com.paywiser.onboarding.kyc.android.sample_app.network.util.ResourceResult
import com.paywiser.onboarding.kyc.android.sample_app.network.util.getResponse
import kotlinx.android.synthetic.main.activity_user_data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class UserDataActivity : AppCompatActivity() {

    lateinit var prefs: SharedPreferences
    lateinit var kycId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_data)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        intent.getStringExtra("KycID").takeIf { !it.isNullOrEmpty() }?.let {
            kycId = it
            kycSuccessful()
        }?: run {
            kycFailed(intent.getStringExtra("FailedReason"))
        }

    }

    private fun kycSuccessful() {
        progressBar.visibility = View.VISIBLE
        tvKycFailed.visibility = View.INVISIBLE
        tvKycSuccessful.visibility = View.INVISIBLE
        scrollView.visibility = View.INVISIBLE
        tvKycError.visibility = View.INVISIBLE
        tvKycErrorMessage.visibility = View.INVISIBLE
        tvKycFailedMessage.visibility = View.INVISIBLE
        GlobalScope.launch {
            getKYCData()
        }
    }

    private fun kycFailed(failedReason: String) {
        tvKycFailed.visibility = View.VISIBLE
        tvKycFailedMessage.visibility = View.VISIBLE
        tvKycSuccessful.visibility = View.INVISIBLE
        scrollView.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
        tvKycError.visibility = View.INVISIBLE
        tvKycErrorMessage.visibility = View.INVISIBLE

        tvKycFailedMessage.text = failedReason
    }


    private suspend fun getKYCData() {
        val requestData = GetKYCDataRequest(UUID.randomUUID().toString(), kycId)

        val result: ResourceResult<GetKYCDataResponse> = withContext(Dispatchers.IO) {
            try {
                OnboardingKycWhiteLabelApiService.createService(prefs.getString("whitelabelEndpointUrl", "")!!, prefs.getString("whitelabelEndpointUsername", "")!!, prefs.getString("whitelabelEndpointPassword", "")!!).getKYCData(requestData).getResponse()
            } catch (ex: Exception) {
                ResourceResult.Failure(ex)
            }
        }

        when(result) {
            is ResourceResult.Success -> {
                when (result.response.StatusCode) {
                    0 -> {
                        withContext(Dispatchers.Main) {
                            progressBar.visibility = View.INVISIBLE
                            tvKycSuccessful.visibility = View.VISIBLE
                            scrollView.visibility = View.VISIBLE
                            showData(result.response)
                        }
                    }
                    else -> {
                        withContext(Dispatchers.Main) {
                            progressBar.visibility = View.INVISIBLE
                            tvKycError.visibility = View.VISIBLE
                            tvKycErrorMessage.visibility = View.VISIBLE
                            tvKycErrorMessage.text = "%s - (%s)".format(
                                result.response.StatusCode,
                                result.response.StatusDescription
                            )
                        }
                    }
                }
            }
            is ResourceResult.Failure -> {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.INVISIBLE
                    tvKycError.visibility = View.VISIBLE
                    tvKycErrorMessage.visibility = View.VISIBLE
                    tvKycErrorMessage.text = result.exception.toString()
                }
            }
        }
    }


    private fun showData(getKYCDataResponse: GetKYCDataResponse) {
       getKYCDataResponse.Process.takeIf { it != null }?.let {process ->

           tvKycProcessModeValue.text = when(process.AttendedMode) {
               true -> "Attended"
               false -> "Unattended"
               else -> "Unknown"
           }

           tvKycProcessUserVerificationValue.text = when(process.UseVerification) {
               true -> "Requested"
               false -> "Not requested"
               else -> "Unknown"
           }

           tvKycProcessStatusValue.text = process.VerificationStatus?:""
       }

        getKYCDataResponse.Document.takeIf { it != null }?.let { document ->
            tvKycDocumentClassificationValue.text = document.Classification?:""
            tvKycDocumentTypeValue.text = document.Type?:""
            tvKycDocumentIssuerValue.text = document.Issuer?:""
            tvKycDocumentExpiryDateValue.text = document.ExpiryDate?:""
            document.PassportNumber.takeIf { !it.isNullOrBlank() }?.let {
                tvKycDocumentNumberValue.text = it
            }
            document.DocumentNumber.takeIf { !it.isNullOrBlank() }?.let {
                tvKycDocumentNumberValue.text = it
            }
            document.Subject.takeIf { it != null }?.let { subject ->
                tvKycDocumentFirstNameValue.text = subject.PrimaryName?:""
                tvKycDocumentLastNameValue.text = subject.SecondaryName?:""
                tvKycDocumentBirthDateValue.text = subject.BirthDate?:""
                tvKycDocumentGenderValue.text = subject.Gender?:""
                tvKycDocumentNationalityValue.text = subject.Nationality?:""
                tvKycDocumentPersonalNumberValue.text = subject.PersonalNumber?:""
            }
        }?: hideDocumentInfo()

        getKYCDataResponse.PersonalData.takeIf { it != null }?.let { personalData ->
            tvKycPersonalDataFirstNameValue.text = personalData.FirstName?:""
            tvKycPersonalDataMiddleNameValue.text = personalData.MiddleName?:""
            tvKycPersonalDataLastNameValue.text = personalData.LastName?:""
            tvKycPersonalDataPhoneNumberValue.text = personalData.MobileNumber?:""
            tvKycPersonalDataEmailValue.text = personalData.Email?:""
            tvKycPersonalDataAddress1Value.text = personalData.Address1?:""
            tvKycPersonalDataAddress2Value.text = personalData.Address2?:""
            tvKycPersonalDataAddress3Value.text = personalData.Address3?:""
            tvKycPersonalDataZipCodeValue.text = personalData.ZipCode?:""
            tvKycPersonalDataCityValue.text = personalData.City?:""
            tvKycPersonalDataStateValue.text = personalData.State?:""
            tvKycPersonalDataCountryValue.text = personalData.CountryName?:""
        }
    }

    private fun hideDocumentInfo() {
        tvKycDocumentNotAvailableMessage.visibility = View.VISIBLE
        tvKycDocumentClassificationTitle.visibility = View.GONE
        tvKycDocumentClassificationValue.visibility = View.GONE
        tvKycDocumentTypeTitle.visibility = View.GONE
        tvKycDocumentTypeValue.visibility = View.GONE
        tvKycDocumentIssuerTitle.visibility = View.GONE
        tvKycDocumentIssuerValue.visibility = View.GONE
        tvKycDocumentExpiryDateTitle.visibility = View.GONE
        tvKycDocumentExpiryDateValue.visibility = View.GONE
        tvKycDocumentNumberTitle.visibility = View.GONE
        tvKycDocumentNumberValue.visibility = View.GONE
        tvKycDocumentFirstNameTitle.visibility = View.GONE
        tvKycDocumentFirstNameValue.visibility = View.GONE
        tvKycDocumentLastNameTitle.visibility = View.GONE
        tvKycDocumentLastNameValue.visibility = View.GONE
        tvKycDocumentBirthDateTitle.visibility = View.GONE
        tvKycDocumentBirthDateValue.visibility = View.GONE
        tvKycDocumentGenderTitle.visibility = View.GONE
        tvKycDocumentGenderValue.visibility = View.GONE
        tvKycDocumentNationalityTitle.visibility = View.GONE
        tvKycDocumentNationalityValue.visibility = View.GONE
        tvKycDocumentPersonalNumberTitle.visibility = View.GONE
        tvKycDocumentPersonalNumberValue.visibility = View.GONE
    }
}