package com.caplaninnovations.looprwallet.models.android.settings

import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.utilities.ApplicationUtility.str
import java.math.BigDecimal

/**
 * Created by Corey on 3/25/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class EthereumFeeSettings(private val looprSettings: LooprSettings) {

    companion object {

        // KEYS
        val KEY_GAS_PRICE = str(R.string.settings_ethereum_fees_gas_price_key)
        val KEY_TRANSFER_ETHER_GAS_LIMIT = str(R.string.settings_ethereum_fees_send_ether_gas_limit_key)
        val KEY_TRANSFER_TOKEN_GAS_LIMIT = str(R.string.settings_ethereum_fees_send_token_gas_limit_key)

        // DEFAULTS
        val DEFAULT_GAS_PRICE = str(R.string.settings_ethereum_fees_gas_price_default_value)
        val DEFAULT_TRANSFER_ETHER_GAS_LIMIT = str(R.string.settings_ethereum_fees_default_send_ether_gas_limit_value)
        val DEFAULT_TRANSFER_TOKEN_GAS_LIMIT = str(R.string.settings_ethereum_fees_default_send_token_gas_limit_value)
    }

    val currentGasPrice: BigDecimal
        get() = getValue(KEY_GAS_PRICE, DEFAULT_GAS_PRICE)

    val currentEthTransferGasLimit: BigDecimal
        get() = getValue(KEY_TRANSFER_ETHER_GAS_LIMIT, DEFAULT_TRANSFER_ETHER_GAS_LIMIT)

    val currentTokenTransferGasLimit: BigDecimal
        get() = getValue(KEY_TRANSFER_TOKEN_GAS_LIMIT, DEFAULT_TRANSFER_TOKEN_GAS_LIMIT)

    // MARK - Private Methods

    private fun getValue(key: String, defaultValue: String): BigDecimal {
        return BigDecimal(looprSettings.getString(key) ?: defaultValue)
    }

}