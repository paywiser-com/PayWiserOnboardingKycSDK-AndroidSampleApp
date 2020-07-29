package com.paywiser.onboarding.kyc.android.sample_app

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.preference.PreferenceManager
import com.paywiser.onboarding.kyc.android.sdk.PayWiserOnboardingKyc
import com.paywiser.onboarding.kyc.android.sdk.data.enums.VideoMode
import com.paywiser.onboarding.kyc.android.sdk.data.model.KycCredentials
import com.paywiser.onboarding.kyc.android.sdk.data.model.KycSettings
import com.paywiser.onboarding.kyc.android.sdk.data.model.KycUserData
import com.paywiser.onboarding.kyc.android.sdk.util.PayWiserOnboardingKycResult
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var prefs: SharedPreferences
    var kycID: String = ""

    companion object {
        const val  KYC_SDK_ACTIVITY_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        setDefaultSettings()

        btnStartKyc.setOnClickListener {
            val videoMode = getVideoMode()
            PayWiserOnboardingKyc.startKyc(this@MainActivity,
                KYC_SDK_ACTIVITY_REQUEST_CODE,
                KycCredentials(prefs.getString("sdkEndpointUrl", "")!!, prefs.getString("sdkEndpointUsername", "")!!, prefs.getString("sdkEndpointPassword", "")!!),
                KycSettings(UUID.randomUUID().toString(), videoMode,  getHumanVerification(videoMode), prefs.getString("language", "en")!!),
                KycUserData(prefs.getString("userDataFirstName", "")!!, prefs.getString("userDataMiddleName", "")!!, prefs.getString("userDataLastName", "")!!, prefs.getString("userDataEmail", "")!!,   prefs.getString("userDataMobileNumber", "")!!, prefs.getString("userDataAddress1", "")!!
                  , prefs.getString("userDataAddress2", "")!!, prefs.getString("userDataAddress3", "")!!, prefs.getString("userDataZipCode", "")!!, prefs.getString("userDataCity", "")!!, prefs.getString("userDataState", "")!!)
            )
        }

        btnRetrieveKycData.setOnClickListener {
            startActivity(Intent(this@MainActivity, UserDataActivity::class.java).apply {
                putExtra("KycID", kycID)
            })
        }
        tvVersion.text = BuildConfig.VERSION_NAME.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == KYC_SDK_ACTIVITY_REQUEST_CODE) {
            when(val result = PayWiserOnboardingKyc.getKycResult(data!!)) {
                is PayWiserOnboardingKycResult.Success -> {
                    kycID = result.kycID
                    startActivity(Intent(this@MainActivity, UserDataActivity::class.java).apply {
                        putExtra("KycID", result.kycID)
                    })
                    btnRetrieveKycData.isEnabled = true
                }
                is PayWiserOnboardingKycResult.Failure -> {
                    startActivity(Intent(this@MainActivity, UserDataActivity::class.java).apply {
                        putExtra("FailedReason", "%s (%s)".format(result.statusCode, result.statusDescription))
                    })
                    btnRetrieveKycData.isEnabled = false
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
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

    private fun getVideoMode(): VideoMode {
        return when(prefs.getBoolean("videoMode", false)) {
            true -> VideoMode.Attended
            false -> VideoMode.Unattended
        }
    }

    private fun getHumanVerification(videoMode: VideoMode): Boolean {
        return when(videoMode) {
            VideoMode.Attended -> false
            VideoMode.Unattended -> prefs.getBoolean("humanVerification", false)
        }
    }

    private fun setDefaultSettings() {

        if (!prefs.getBoolean("settingsSet", false)) {
            val editor = prefs.edit()
            editor.putString("whitelabelEndpointUrl", "https://onboarding-kyc-dev.paywiser.eu/Whitelabel/")
            editor.putString("whitelabelEndpointUsername", "aaa")
            editor.putString("whitelabelEndpointPassword", "bbb")
            editor.putString("sdkEndpointUrl", "https://onboarding-kyc-dev.paywiser.eu/VideoID/")
            editor.putString("sdkEndpointUsername", "111")
            editor.putString("sdkEndpointPassword", "222")
            editor.putBoolean("videoMode", false)
            editor.putBoolean("humanVerification", false)
            editor.putString("language", "en")
            editor.putBoolean("settingsSet", true)
            editor.commit()
        }

    }
}