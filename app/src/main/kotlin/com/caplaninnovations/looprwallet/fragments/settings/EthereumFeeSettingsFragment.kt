package com.caplaninnovations.looprwallet.fragments.settings

import android.os.Bundle
import android.support.v7.preference.Preference
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.models.android.settings.EthereumFeeSettings
import com.caplaninnovations.looprwallet.models.android.settings.EthereumFeeSettings.Companion.DEFAULT_GAS_PRICE
import com.caplaninnovations.looprwallet.models.android.settings.EthereumFeeSettings.Companion.DEFAULT_TRANSFER_ETHER_GAS_LIMIT
import com.caplaninnovations.looprwallet.models.android.settings.EthereumFeeSettings.Companion.DEFAULT_TRANSFER_TOKEN_GAS_LIMIT
import com.caplaninnovations.looprwallet.models.android.settings.EthereumFeeSettings.Companion.KEY_GAS_PRICE
import com.caplaninnovations.looprwallet.models.android.settings.EthereumFeeSettings.Companion.KEY_TRANSFER_ETHER_GAS_LIMIT
import com.caplaninnovations.looprwallet.models.android.settings.EthereumFeeSettings.Companion.KEY_TRANSFER_TOKEN_GAS_LIMIT
import com.caplaninnovations.looprwallet.utilities.ApplicationUtility
import javax.inject.Inject

/**
 * Created by Corey on 3/26/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
class EthereumFeeSettingsFragment : BaseSettingsFragment() {

    companion object {

        val TAG: String = EthereumFeeSettingsFragment::class.java.simpleName
    }

    override val fragmentTitle = ApplicationUtility.str(R.string.ethereum_fees)

    @Inject
    lateinit var ethereumFeeSettings: EthereumFeeSettings

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

    override fun onPreferenceClick(preference: Preference?): Boolean {
        TODO("not implemented") // TODO
    }

    override fun onPreferenceValueChange(preference: Preference, value: String): Boolean {
        TODO("not implemented") // TODO
    }

    override fun getSummaryValue(preference: Preference, value: String): String {
        TODO("not implemented") // TODO
    }

}