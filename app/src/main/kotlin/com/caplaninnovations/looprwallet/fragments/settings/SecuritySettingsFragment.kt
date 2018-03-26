package com.caplaninnovations.looprwallet.fragments.settings

import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.models.android.settings.SecuritySettings
import com.caplaninnovations.looprwallet.models.android.settings.SecuritySettings.Companion.PREFERENCE_KEY_SECURITY_TYPE
import com.caplaninnovations.looprwallet.models.android.settings.SecuritySettings.Companion.TYPE_DEFAULT_VALUE_SECURITY
import com.caplaninnovations.looprwallet.models.android.settings.SecuritySettings.Companion.TYPE_PIN_SECURITY
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

    @Inject
    lateinit var securitySettings: SecuritySettings

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        LooprWalletApp.dagger.inject(this)

        addPreferencesFromResource(R.xml.settings_security)
    }

    override fun getPreferenceKeysAndDefaultValuesForListeners() = listOf(
            Pair(PREFERENCE_KEY_SECURITY_TYPE, TYPE_DEFAULT_VALUE_SECURITY),
            Pair("world-key", "HELLO WORLD!!!!")
    )

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        val key = preference?.key ?: return false
        val stringValue = newValue?.toString() ?: return false

        super.onPreferenceChange(preference, newValue)

        return when (key) {
            PREFERENCE_KEY_SECURITY_TYPE -> {
                if (stringValue == TYPE_DEFAULT_VALUE_SECURITY) {
                    onSecurityScreenDisabled()
                } else if (stringValue == TYPE_PIN_SECURITY) {
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
        securitySettings.setCurrentSecurityType(TYPE_DEFAULT_VALUE_SECURITY)

        val preference = findPreference(PREFERENCE_KEY_SECURITY_TYPE) as ListPreference
        bindListPreferenceValue(preference, TYPE_DEFAULT_VALUE_SECURITY)

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

        val preference = findPreference(PREFERENCE_KEY_SECURITY_TYPE) as ListPreference
        bindListPreferenceValue(preference, TYPE_PIN_SECURITY)

        getPreferenceKeysAndDefaultValuesForListeners().forEach {
            if (it.first != PREFERENCE_KEY_SECURITY_TYPE) {
                findPreference(it.first).isEnabled = true
            }
        }
    }

}