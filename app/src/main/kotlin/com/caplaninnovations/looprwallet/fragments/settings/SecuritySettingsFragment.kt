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
import com.caplaninnovations.looprwallet.fragments.security.OnSecurityChangeListener
import com.caplaninnovations.looprwallet.models.android.settings.SecuritySettings
import com.caplaninnovations.looprwallet.models.android.settings.SecuritySettings.Companion.DEFAULT_SECURITY_TIMEOUT
import com.caplaninnovations.looprwallet.models.android.settings.SecuritySettings.Companion.KEY_SECURITY_TIMEOUT
import com.caplaninnovations.looprwallet.models.android.settings.SecuritySettings.Companion.KEY_SECURITY_TYPE
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
class SecuritySettingsFragment : BaseSettingsFragment(), OnSecurityChangeListener {

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

    override fun getPreferenceKeysAndDefaultValues() = listOf(
            Pair(KEY_SECURITY_TYPE, TYPE_DEFAULT_VALUE_SECURITY),
            Pair(KEY_SECURITY_TIMEOUT, DEFAULT_SECURITY_TIMEOUT)
    )

    override fun onPreferenceValueChange(preference: Preference, value: String): Boolean {
        return when (preference.key) {
            KEY_SECURITY_TYPE -> {
                val currentSecurityType = securitySettings.getCurrentSecurityType()
                if (value == currentSecurityType && value == TYPE_DEFAULT_VALUE_SECURITY) {
                    // We aren't changing the preference and the type of security is set to NONE
                    return false
                }

                val fragment: BaseSecurityFragment
                val tag: String
                when (value) {
                    TYPE_DEFAULT_VALUE_SECURITY -> {
                        fragment = ConfirmOldSecurityFragment.createDisableSecurityInstance()
                        tag = ConfirmOldSecurityFragment.TAG
                    }
                    TYPE_PIN_SECURITY -> when (currentSecurityType) {
                        TYPE_PIN_SECURITY -> {
                            // We currently use a PIN, and we reselected PIN
                            fragment = ConfirmOldSecurityFragment.createChangeSecuritySettings()
                            tag = ConfirmOldSecurityFragment.TAG
                        }
                        else -> {
                            // We are setting a PIN for the first time
                            fragment = EnterNewSecurityFragment.createPinInstance()
                            tag = EnterNewSecurityFragment.TAG
                        }
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
        KEY_SECURITY_TYPE -> getSummaryForListPreference(preference as ListPreference, value)
        KEY_SECURITY_TIMEOUT -> {
            val uiEntry = getSummaryForListPreference(preference as ListPreference, value)
            val summaryFormatter = ApplicationUtility.str(R.string.formatter_automatically_lock_after_timeout)
            String.format(summaryFormatter, uiEntry)
        }
        else -> throw IllegalArgumentException("Invalid preference key, found: ${preference.key}")
    }

    override fun onPreferenceClick(preference: Preference?) = false

    override fun onSecurityEnabled(securityType: String) {
        securitySettings.setCurrentSecurityType(securityType)

        (findPreference(KEY_SECURITY_TYPE) as? ListPreference)?.let {
            it.summary = getSummaryForListPreference(it, TYPE_PIN_SECURITY)
        }

        findPreference(KEY_SECURITY_TIMEOUT)?.let {
            it.summary = getSummaryValue(it, securitySettings.getCurrentSecurityTimeout().toString())
            it.isEnabled = true
        }

        getPreferenceKeysAndDefaultValues().forEach {
            if (it.first != KEY_SECURITY_TYPE) {
                findPreference(it.first).isEnabled = true
            }
        }
    }

    override fun onSecurityDisabled() {
        securitySettings.setCurrentSecurityType(TYPE_DEFAULT_VALUE_SECURITY)

        (findPreference(KEY_SECURITY_TYPE) as? ListPreference)?.let {
            it.summary = getSummaryForListPreference(it, TYPE_DEFAULT_VALUE_SECURITY)
        }

        findPreference(KEY_SECURITY_TIMEOUT)?.let {
            it.summary = getString(R.string.disabled_application_lock)
            it.isEnabled = false
        }


        getPreferenceKeysAndDefaultValues().forEach {
            if (it.first != KEY_SECURITY_TYPE) {
                findPreference(it.first)?.isEnabled = false
            }
        }
    }

}