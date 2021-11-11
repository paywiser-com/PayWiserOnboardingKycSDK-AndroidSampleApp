package com.paywiser.onboarding.kyc.android.sample_app

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.paywiser.onboarding.kyc.android.sdk.PayWiserOnboardingKyc
import com.paywiser.onboarding.kyc.android.sdk.data.model.KycCredentials
import com.paywiser.onboarding.kyc.android.sdk.data.model.KycSettings
import com.paywiser.onboarding.kyc.android.sdk.data.model.KycUserData
import com.paywiser.onboarding.kyc.android.sdk.util.PayWiserOnboardingKycResult
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var prefs: SharedPreferences

    companion object {
        const val  KYC_SDK_ACTIVITY_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        setDefaultSettings()

        btnStartKyc.setOnClickListener {
            PayWiserOnboardingKyc.startKyc(this@MainActivity,
                KYC_SDK_ACTIVITY_REQUEST_CODE,
                KycCredentials(prefs.getString("sdkEndpointUrl", "")!!, prefs.getString("sdkEndpointUsername", "")!!, prefs.getString("sdkEndpointPassword", "")!!),
                KycSettings(UUID.randomUUID().toString(), prefs.getString("language", "en")!!, prefs.getString("referenceNumber", null)),
                KycUserData(prefs.getString("userDataFirstName", "")!!, prefs.getString("userDataMiddleName", "")!!, prefs.getString("userDataLastName", "")!!, prefs.getString("userDataEmail", "")!!,   prefs.getString("userDataMobileNumber", "")!!, prefs.getString("userDataAddress1", "")!!
                  , prefs.getString("userDataAddress2", "")!!, prefs.getString("userDataAddress3", "")!!, prefs.getString("userDataZipCode", "")!!, prefs.getString("userDataCity", "")!!, prefs.getString("userDataState", "")!!, prefs.getString("userDataCountry", "")!!)
            )
        }

        tvVersion.text = BuildConfig.VERSION_NAME.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == KYC_SDK_ACTIVITY_REQUEST_CODE) {
            when(val result = PayWiserOnboardingKyc.getKycResult(data!!)) {
                is PayWiserOnboardingKycResult.Success -> {

                    tvStatus.text = "Successfull"
                    tvStatus.setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_green_dark))
                    tvKycReferenceID.text = result.kycReferenceID?:""
                    tvReferenceNumber.text = result.referenceNumber?:""
                    tvPersonID.text = result.personID?:""
                    tvKycID.text = result.kycID?:""

                }
                is PayWiserOnboardingKycResult.Failure -> {
                    tvStatus.text = "Failed with status code: %s (%s)".format(result.statusCode, result.statusDescription)
                    tvStatus.setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_red_dark))
                    tvKycReferenceID.text = result.kycReferenceID?:""
                    tvReferenceNumber.text = result.referenceNumber?:""
                    tvPersonID.text = result.personID?:""
                    tvKycID.text = result.kycID?:""
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setDefaultSettings() {

        if (!prefs.getBoolean("settingsSet", false)) {
            val editor = prefs.edit()
            editor.putString("sdkEndpointUrl", "https://onboarding-kyc-dev.paywiser.eu/mobile/")
            editor.putString("sdkEndpointUsername", "111")
            editor.putString("sdkEndpointPassword", "222")
            editor.putString("language", "en")
            editor.putBoolean("settingsSet", true)
            editor.commit()
        }

    }
}