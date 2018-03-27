package com.caplaninnovations.looprwallet.fragments.settings

import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.SettingsActivity
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.fragments.security.BaseSecurityFragment
import com.caplaninnovations.looprwallet.fragments.security.ConfirmOldSecurityFragment
import com.caplaninnovations.looprwallet.fragments.security.EnterNewSecurityFragment
import com.caplaninnovations.looprwallet.models.android.settings.SecuritySettings
import com.caplaninnovations.looprwallet.models.android.settings.SecuritySettings.Companion.DEFAULT_SECURITY_TIMEOUT
import com.caplaninnovations.looprwallet.models.android.settings.SecuritySettings.Companion.PREFERENCE_KEY_SECURITY_TIMEOUT
import com.caplaninnovations.looprwallet.models.android.settings.SecuritySettings.Companion.PREFERENCE_KEY_SECURITY_TYPE
import com.caplaninnovations.looprwallet.models.android.settings.SecuritySettings.Companion.TYPE_DEFAULT_VALUE_SECURITY
import com.caplaninnovations.looprwallet.models.android.settings.SecuritySettings.Companion.TYPE_PIN_SECURITY
import com.caplaninnovations.looprwallet.utilities.ApplicationUtility
import javax.inject.Inject


/**
 * Created by Corey on 3/24/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: For showing nested settings screens
 */
class SecuritySettingsFragment : BaseSettingsFragment() {

    companion object {

        val TAG: String = SecuritySettingsFragment::class.java.simpleName
    }

    override val fragmentTitle = ApplicationUtility.str(R.string.security)

    @Inject
    lateinit var securitySettings: SecuritySettings

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        LooprWalletApp.dagger.inject(this)

        addPreferencesFromResource(R.xml.settings_security)
    }

    override fun getPreferenceKeysAndDefaultValuesForListeners() = listOf(
            Pair(PREFERENCE_KEY_SECURITY_TYPE, TYPE_DEFAULT_VALUE_SECURITY),
            Pair(PREFERENCE_KEY_SECURITY_TIMEOUT, DEFAULT_SECURITY_TIMEOUT)
    )

    override fun onPreferenceValueChange(preference: Preference, value: String): Boolean {
        return when (preference.key) {
            PREFERENCE_KEY_SECURITY_TYPE -> {
                val fragment: BaseSecurityFragment
                val tag: String
                when (value) {
                    TYPE_DEFAULT_VALUE_SECURITY -> {
                        fragment = ConfirmOldSecurityFragment.createDisableSecurityInstance()
                        tag = ConfirmOldSecurityFragment.TAG
                    }
                    TYPE_PIN_SECURITY -> {
                        fragment = EnterNewSecurityFragment.createPinInstance()
                        tag = EnterNewSecurityFragment.TAG
                    }
                    else -> throw IllegalArgumentException("Invalid security type, found: $value")
                }
                (activity as? SettingsActivity)?.onSecuritySettingsFragmentClick(fragment, tag)
                false
            }
            else -> {
                return true
            }
        }
    }

    override fun getSummaryValue(preference: Preference, value: String) = when (preference.key) {
        PREFERENCE_KEY_SECURITY_TYPE -> getSummaryForListPreference(preference as ListPreference, value)
        PREFERENCE_KEY_SECURITY_TIMEOUT -> {
            val uiEntry = getSummaryForListPreference(preference as ListPreference, value)
            val summaryFormatter = ApplicationUtility.str(R.string.formatter_automatically_lock_after_timeout)
            String.format(summaryFormatter, uiEntry)
        }
        else -> throw IllegalArgumentException("Invalid preference key, found: ${preference.key}")
    }

    override fun onPreferenceClick(preference: Preference?) = false

    /**
     * Called when the security screen is now *disabled*, which will disable other settings in the
     * screen.
     */
    fun onSecurityScreenDisabled() {
        securitySettings.setCurrentSecurityType(TYPE_DEFAULT_VALUE_SECURITY)

        val securityTypePreference = findPreference(PREFERENCE_KEY_SECURITY_TYPE) as ListPreference
        securityTypePreference.summary = getSummaryForListPreference(securityTypePreference, TYPE_DEFAULT_VALUE_SECURITY)

        val timeoutPreference = findPreference(PREFERENCE_KEY_SECURITY_TIMEOUT)
        val summary = getString(R.string.disabled_application_lock)
        timeoutPreference.summary = summary
        timeoutPreference.isEnabled = false

        getPreferenceKeysAndDefaultValuesForListeners().forEach {
            if (it.first != PREFERENCE_KEY_SECURITY_TYPE) {
                findPreference(it.first).isEnabled = false
            }
        }
    }

    /**
     * Called when the security screen is now *enabled*, which will enable other settings in the
     * screen.
     */
    fun onSecurityScreenEnabled(securityType: String) {
        securitySettings.setCurrentSecurityType(securityType)

        val securityTypePreference = findPreference(PREFERENCE_KEY_SECURITY_TYPE) as ListPreference
        securityTypePreference.summary = getSummaryForListPreference(securityTypePreference, TYPE_PIN_SECURITY)

        val timeoutPreference = findPreference(PREFERENCE_KEY_SECURITY_TIMEOUT)
        val summary = getSummaryValue(timeoutPreference, securitySettings.getCurrentSecurityTimeout().toString())
        timeoutPreference.summary = summary
        timeoutPreference.isEnabled = true

        getPreferenceKeysAndDefaultValuesForListeners().forEach {
            if (it.first != PREFERENCE_KEY_SECURITY_TYPE) {
                findPreference(it.first).isEnabled = true
            }
        }
    }

}