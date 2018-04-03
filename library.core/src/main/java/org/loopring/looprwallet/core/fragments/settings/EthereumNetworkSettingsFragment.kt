package org.loopring.looprwallet.core.fragments.settings

import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.models.settings.EthereumNetworkSettings.Companion.DEFAULT_VALUE_NODE
import org.loopring.looprwallet.core.models.settings.EthereumNetworkSettings.Companion.KEY_NODE
import org.loopring.looprwallet.core.utilities.ApplicationUtility

/**
 * Created by Corey on 3/26/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class EthereumNetworkSettingsFragment : BaseSettingsFragment() {

    companion object {

        val TAG: String = EthereumNetworkSettingsFragment::class.java.simpleName
    }

    override val fragmentTitle = ApplicationUtility.str(R.string.ethereum_network)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_ethereum_network)
    }

    override fun getPreferenceKeysAndDefaultValues() = listOf(
            KEY_NODE to DEFAULT_VALUE_NODE
    )

    override fun onPreferenceValueChange(preference: Preference, value: String) = when (preference.key) {
        KEY_NODE -> true
        else -> throw IllegalArgumentException("Invalid preference key, found: ${preference.key}")
    }

    override fun getSummaryValue(preference: Preference, value: String) = when (preference.key) {
        KEY_NODE -> getSummaryForListPreference(preference as ListPreference, value)
        else -> throw IllegalArgumentException("Invalid preference key, found: ${preference.key}")
    }

    override fun onPreferenceClick(preference: Preference?) = false

}