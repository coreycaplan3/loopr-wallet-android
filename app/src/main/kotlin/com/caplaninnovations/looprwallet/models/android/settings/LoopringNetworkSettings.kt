package com.caplaninnovations.looprwallet.models.android.settings

import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.utilities.ApplicationUtility.str

/**
 * Created by Corey on 3/25/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: Settings related to the Loopring network, like selecting to which relay to
 * connect, or the smart contract version.
 */
class LoopringNetworkSettings(private val looprSettings: LooprSettings) {

    companion object {

        val KEY_RELAY = str(R.string.settings_loopring_network_relay_key)
        val KEY_CONTRACT_VERSION = str(R.string.settings_loopring_network_contract_version_key)

        val DEFAULT_VALUE_RELAY = str(R.string.settings_loopring_network_relay_default_value)
        val DEFAULT_VALUE_CONTRACT_VERSION = str(R.string.settings_loopring_network_contract_version_default_value)
    }

    val currentRelay: String
        get() = looprSettings.getString(KEY_RELAY) ?: DEFAULT_VALUE_RELAY

    val currentContractVersion: String
        get() = looprSettings.getString(KEY_CONTRACT_VERSION) ?: DEFAULT_VALUE_CONTRACT_VERSION

}