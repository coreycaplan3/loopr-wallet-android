package org.loopring.looprwallet.settings.fragments

import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import com.caplaninnovations.looprwallet.R
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.models.settings.CurrencySettings.Companion.DEFAULT_VALUE_CURRENCY
import org.loopring.looprwallet.core.models.settings.CurrencySettings.Companion.DEFAULT_VALUE_REFRESH_FREQUENCY
import org.loopring.looprwallet.core.models.settings.CurrencySettings.Companion.KEY_CURRENT_CURRENCY
import org.loopring.looprwallet.core.models.settings.CurrencySettings.Companion.KEY_REFRESH_FREQUENCY
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str

/**
 * Created by Corey on 3/26/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
class CurrencySettingsFragment : BaseSettingsFragment() {

    companion object {

        val TAG: String = CurrencySettingsFragment::class.java.simpleName
    }

    override val fragmentTitle = str(R.string.currency_defaults)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_currency)
    }

    override fun getPreferenceKeysAndDefaultValues() = listOf(
            KEY_CURRENT_CURRENCY to DEFAULT_VALUE_CURRENCY,
            KEY_REFRESH_FREQUENCY to DEFAULT_VALUE_REFRESH_FREQUENCY
    )

    override fun onPreferenceValueChange(preference: Preference, value: String) = when (preference.key) {
        KEY_CURRENT_CURRENCY -> {
            getPreferenceKeysAndDefaultValues().forEach {
                if (it.first != KEY_CURRENT_CURRENCY) {
                    val otherPreferences = findPreference(CurrencySettings.KEY_REFRESH_FREQUENCY)
                    otherPreferences.isEnabled = value != CurrencySettings.DEFAULT_VALUE_CURRENCY
                }
            }
            true
        }
        KEY_REFRESH_FREQUENCY -> true
        else -> throw IllegalArgumentException("Invalid key, found: ${preference.key}")
    }

    override fun getSummaryValue(preference: Preference, value: String) = when (preference.key) {
        KEY_CURRENT_CURRENCY -> value
        KEY_REFRESH_FREQUENCY -> {
            if (preference.isEnabled && preference is ListPreference) {
                getSummaryForListPreference(preference, value)
            } else {
                str(R.string.this_setting_is_disabled_usd_selected)
            }
        }
        else -> throw IllegalArgumentException("Invalid key, found: ${preference.key}")
    }

    override fun onPreferenceClick(preference: Preference?) = false

}