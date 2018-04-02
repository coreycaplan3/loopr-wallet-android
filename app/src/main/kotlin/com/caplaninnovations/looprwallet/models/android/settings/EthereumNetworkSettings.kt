package com.caplaninnovations.looprwallet.models.android.settings

import com.caplaninnovations.looprwallet.R
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.utilities.ApplicationUtility.strArray

/**
 * Created by Corey on 3/25/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: Settings related to the ethereum network, like choosing to which node to
 * connect and adding custom nodes.
 */
class EthereumNetworkSettings(private val looprSettings: LooprSettings) {

    companion object {

        val KEY_NODE = str(R.string.settings_ethereum_network_node_key)

        val DEFAULT_VALUE_NODE = str(R.string.settings_ethereum_network_node_default_value)

        val ARRAY_NODE = strArray(R.array.settings_ethereum_network_node_entries_values)
    }

}