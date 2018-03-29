package com.caplaninnovations.looprwallet.fragments.settings

import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.SeekBarPreference
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.models.android.settings.CurrencySettings
import com.caplaninnovations.looprwallet.models.android.settings.EthereumFeeSettings.Companion.DEFAULT_GAS_PRICE
import com.caplaninnovations.looprwallet.models.android.settings.EthereumFeeSettings.Companion.DEFAULT_TRANSFER_ETHER_GAS_LIMIT
import com.caplaninnovations.looprwallet.models.android.settings.EthereumFeeSettings.Companion.DEFAULT_TRANSFER_TOKEN_GAS_LIMIT
import com.caplaninnovations.looprwallet.models.android.settings.EthereumFeeSettings.Companion.KEY_GAS_PRICE
import com.caplaninnovations.looprwallet.models.android.settings.EthereumFeeSettings.Companion.KEY_TRANSFER_ETHER_GAS_LIMIT
import com.caplaninnovations.looprwallet.models.android.settings.EthereumFeeSettings.Companion.KEY_TRANSFER_TOKEN_GAS_LIMIT
import com.caplaninnovations.looprwallet.utilities.ApplicationUtility
import com.caplaninnovations.looprwallet.utilities.ApplicationUtility.str
import javax.inject.Inject

/**
 * Created by Corey on 3/26/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class EthereumFeeSettingsFragment : BaseSettingsFragment() {

    companion object {

        val TAG: String = EthereumFeeSettingsFragment::class.java.simpleName
    }

    override val fragmentTitle = ApplicationUtility.str(R.string.ethereum_fees)

    @Inject
    lateinit var currencySettings: CurrencySettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LooprWalletApp.dagger.inject(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_ethereum_fees)
    }

    override fun getPreferenceKeysAndDefaultValuesForListeners() = listOf(
            KEY_GAS_PRICE to DEFAULT_GAS_PRICE,
            KEY_TRANSFER_ETHER_GAS_LIMIT to DEFAULT_TRANSFER_ETHER_GAS_LIMIT,
            KEY_TRANSFER_TOKEN_GAS_LIMIT to DEFAULT_TRANSFER_TOKEN_GAS_LIMIT
    )

    override fun onPreferenceClick(preference: Preference?) = false

    override fun onPreferenceValueChange(preference: Preference, value: String): Boolean {
        when (preference) {
            is SeekBarPreference -> when (preference.key) {
                KEY_GAS_PRICE -> {
                    preference.value = value.toInt()
                }
            }
        }
        return true
    }

    override fun getSummaryValue(preference: Preference, value: String) = when (preference.key) {
        KEY_GAS_PRICE -> getSummaryForGasPrice(value.toInt())
        KEY_TRANSFER_ETHER_GAS_LIMIT -> formatStringValueAsNumber(value)
        KEY_TRANSFER_TOKEN_GAS_LIMIT -> formatStringValueAsNumber(value)
        else -> throw IllegalArgumentException("Invalid key, found: ${preference.key}")
    }

    // MARK -

    private fun formatStringValueAsNumber(value: String): String {
        return currencySettings.getNumberFormatter().format(value.toDouble())
    }

    private fun getSummaryForGasPrice(value: Int) = when {
        value == 1 -> str(R.string.very_slow_and_very_cheap)
        value <= 5 -> str(R.string.slow_and_cheap)
        value <= 10 -> str(R.string.average_speed_and_average_price)
        value <= 20 -> str(R.string.fast_and_pricey)
        value <= 50 -> str(R.string.very_fast_very_expensive)
        else -> throw IllegalArgumentException("Invalid value, found: $value")
    }

}