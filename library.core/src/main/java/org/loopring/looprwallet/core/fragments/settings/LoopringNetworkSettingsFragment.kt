package org.loopring.looprwallet.core.fragments.settings

import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.models.settings.LoopringNetworkSettings.Companion.DEFAULT_VALUE_CONTRACT_VERSION
import org.loopring.looprwallet.core.models.settings.LoopringNetworkSettings.Companion.DEFAULT_VALUE_RELAY
import org.loopring.looprwallet.core.models.settings.LoopringNetworkSettings.Companion.KEY_CONTRACT_VERSION
import org.loopring.looprwallet.core.models.settings.LoopringNetworkSettings.Companion.KEY_RELAY
import org.loopring.looprwallet.core.utilities.ApplicationUtility

/**
 * Created by Corey on 3/26/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class LoopringNetworkSettingsFragment : BaseSettingsFragment() {

    companion object {

        val TAG: String = LoopringNetworkSettingsFragment::class.java.simpleName
    }

    override val fragmentTitle = ApplicationUtility.str(R.string.loopring_network)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)
        addPreferencesFromResource(R.xml.settings_loopring_network)
    }

    override fun getPreferenceKeysAndDefaultValues() = listOf(
            KEY_RELAY to DEFAULT_VALUE_RELAY,
            KEY_CONTRACT_VERSION to DEFAULT_VALUE_CONTRACT_VERSION
    )

    override fun onPreferenceValueChange(preference: Preference, value: String) = when (preference.key) {
        KEY_RELAY -> true
        KEY_CONTRACT_VERSION -> true
        else -> throw IllegalArgumentException("Invalid key, found: ${preference.key}")
    }

    override fun getSummaryValue(preference: Preference, value: String) = when(preference.key) {
        KEY_RELAY -> getSummaryForListPreference(preference as ListPreference, value)
        KEY_CONTRACT_VERSION -> getSummaryForListPreference(preference as ListPreference, value)
        else -> throw IllegalArgumentException("Invalid key, found: ${preference.key}")
    }

    override fun onPreferenceClick(preference: Preference?) = false

}