package com.caplaninnovations.looprwallet.fragments.settings

import android.os.Bundle
import android.support.v7.preference.Preference
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.android.settings.GeneralWalletSettings.Companion.DEFAULT_VALUE_SHOW_ZERO_BALANCES
import com.caplaninnovations.looprwallet.models.android.settings.GeneralWalletSettings.Companion.KEY_SHOW_ZERO_BALANCES
import com.caplaninnovations.looprwallet.utilities.ApplicationUtility.str

/**
 * Created by Corey on 3/26/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class GeneralWalletSettingsFragment : BaseSettingsFragment() {

    companion object {

        val TAG: String = GeneralWalletSettingsFragment::class.java.simpleName
    }

    override val fragmentTitle = str(R.string.wallet_customizations)

    override fun getPreferenceKeysAndDefaultValuesForListeners() = listOf(
            KEY_SHOW_ZERO_BALANCES to DEFAULT_VALUE_SHOW_ZERO_BALANCES
    )

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_general_wallet)
    }

    override fun onPreferenceValueChange(preference: Preference, value: String) = when (preference.key) {
        KEY_SHOW_ZERO_BALANCES -> true
        else -> throw IllegalArgumentException("Invalid key, found: ${preference.key}")
    }

    override fun onPreferenceClick(preference: Preference?) = false

    override fun getSummaryValue(preference: Preference, value: String) = when (preference.key) {
        KEY_SHOW_ZERO_BALANCES -> when {
            value.toBoolean() -> str(R.string.shown_initially)
            else -> str(R.string.hidden_initially)
        }
        else -> throw IllegalArgumentException("Invalid key, found: ${preference.key}")
    }

}