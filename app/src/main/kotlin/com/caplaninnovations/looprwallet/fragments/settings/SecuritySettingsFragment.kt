package com.caplaninnovations.looprwallet.fragments.settings

import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.extensions.loge
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettings
import com.caplaninnovations.looprwallet.utilities.ApplicationUtility


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

        val PREFERENCE_KEY_SECURITY_TYPE = ApplicationUtility.str(R.string.settings_security_type_key)
        val PREFERENCE_KEY_SECURITY_TIMEOUT = ApplicationUtility.str(R.string.settings_security_timeout_key)

        // Security Types
        val DEFAULT_VALUE_SECURITY_TYPE = ApplicationUtility.str(R.string.settings_security_type_entries_values_default)
        val PIN_VALUE_SECURITY_TYPE = ApplicationUtility.str(R.string.settings_security_type_entries_values_pin)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_security)
    }

    override fun getPreferenceKeysAndDefaultValuesForListeners() = listOf(
            Pair(PREFERENCE_KEY_SECURITY_TYPE, DEFAULT_VALUE_SECURITY_TYPE),
            Pair("world-key", "HELLO WORLD!!!!")
    )

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        val key = preference?.key ?: return false
        val stringValue = newValue?.toString() ?: return false

        super.onPreferenceChange(preference, newValue)

        return when (key) {
            PREFERENCE_KEY_SECURITY_TYPE -> {
                if (stringValue == DEFAULT_VALUE_SECURITY_TYPE) {
                    onSecurityScreenDisabled()
                } else if (stringValue == PIN_VALUE_SECURITY_TYPE) {
                    onSecurityScreenEnabled(stringValue)
                }
                false
            }
            else -> {
                return true
            }
        }
    }

    override fun onPreferenceClick(preference: Preference?) = false

    /**
     * Called when the security screen is now *disabled*, which will disable other settings in the
     * screen.
     */
    fun onSecurityScreenDisabled() {
        LooprSettings.getInstance(context!!)
                .putString(PREFERENCE_KEY_SECURITY_TYPE, DEFAULT_VALUE_SECURITY_TYPE)

        val preference = findPreference(PREFERENCE_KEY_SECURITY_TYPE) as ListPreference
        bindListPreferenceValue(preference, DEFAULT_VALUE_SECURITY_TYPE)

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
        LooprSettings.getInstance(context!!)
                .putString(PREFERENCE_KEY_SECURITY_TYPE, getSecurityType(securityType))

        val preference = findPreference(PREFERENCE_KEY_SECURITY_TYPE) as ListPreference
        bindListPreferenceValue(preference, PIN_VALUE_SECURITY_TYPE)

        getPreferenceKeysAndDefaultValuesForListeners().forEach {
            if (it.first != PREFERENCE_KEY_SECURITY_TYPE) {
                findPreference(it.first).isEnabled = true
            }
        }
    }

    // MARK - Private Methods

    private fun getSecurityType(value: String)= when(value) {
        PIN_VALUE_SECURITY_TYPE -> value
        else -> {
            loge("Invalid security type, found: $value")
            DEFAULT_VALUE_SECURITY_TYPE
        }
    }

}