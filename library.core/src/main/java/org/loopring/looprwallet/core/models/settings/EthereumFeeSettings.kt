package org.loopring.looprwallet.core.models.settings

import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.utilities.ApplicationUtility.int
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import java.math.BigInteger

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
        val KEY_DEPOSIT_WETH_GAS_LIMIT = str(R.string.settings_ethereum_fees_deposit_weth_gas_limit_key)
        val KEY_CANCEL_ALL_ORDERS_GAS_LIMIT = str(R.string.settings_ethereum_fees_cancel_all_orders_gas_limit_key)
        val KEY_CANCEL_ORDER_GAS_LIMIT = str(R.string.settings_ethereum_fees_cancel_order_gas_limit_key)
        val KEY_CANCEL_ORDERS_BY_TRADING_PAIR_GAS_LIMIT = str(R.string.settings_ethereum_fees_cancel_orders_by_trading_pair_gas_limit_key)

        // DEFAULTS
        val DEFAULT_GAS_PRICE = int(R.integer.settings_ethereum_fees_gas_price_default_value).toString()
        val DEFAULT_TRANSFER_ETHER_GAS_LIMIT = str(R.string.settings_ethereum_fees_default_send_ether_gas_limit_value)
        val DEFAULT_TRANSFER_TOKEN_GAS_LIMIT = str(R.string.settings_ethereum_fees_default_send_token_gas_limit_value)
        val DEFAULT_DEPOSIT_WETH_GAS_LIMIT = str(R.string.settings_ethereum_fees_default_deposit_weth_gas_limit_value)
        val DEFAULT_CANCEL_ALL_ORDERS_GAS_LIMIT = str(R.string.settings_ethereum_fees_default_deposit_cancel_all_orders_gas_limit_value)
        val DEFAULT_CANCEL_ORDER_GAS_LIMIT = str(R.string.settings_ethereum_fees_default_deposit_cancel_order_gas_limit_value)
        val DEFAULT_CANCEL_ORDERS_BY_TRADING_PAIR_GAS_LIMIT = str(R.string.settings_ethereum_fees_default_deposit_cancel_orders_by_trading_pair_gas_limit_value)
    }

    val convertGasPriceToWei: BigInteger
        get() = gasPriceInGwei * BigInteger("1000000000")

    /**
     * The gas price, formatted in GWEI - Must be converted to WEI for like-comparisons with
     * gasLimits
     */
    val gasPriceInGwei: BigInteger
        get() = getValue(KEY_GAS_PRICE, DEFAULT_GAS_PRICE)

    val ethTransferGasLimit: BigInteger
        get() = getValue(KEY_TRANSFER_ETHER_GAS_LIMIT, DEFAULT_TRANSFER_ETHER_GAS_LIMIT)

    val tokenTransferGasLimit: BigInteger
        get() = getValue(KEY_TRANSFER_TOKEN_GAS_LIMIT, DEFAULT_TRANSFER_TOKEN_GAS_LIMIT)

    val wethDepositGasLimit: BigInteger
        get() = getValue(KEY_DEPOSIT_WETH_GAS_LIMIT, DEFAULT_DEPOSIT_WETH_GAS_LIMIT)

    val cancelAllOrdersGasLimit: BigInteger
        get() = getValue(KEY_CANCEL_ALL_ORDERS_GAS_LIMIT, DEFAULT_CANCEL_ALL_ORDERS_GAS_LIMIT)

    val cancelOrderGasLimit: BigInteger
        get() = getValue(KEY_CANCEL_ORDER_GAS_LIMIT, DEFAULT_CANCEL_ORDER_GAS_LIMIT)

    val cancelOrdersByTradingPair: BigInteger
        get() = getValue(KEY_CANCEL_ORDERS_BY_TRADING_PAIR_GAS_LIMIT, DEFAULT_CANCEL_ORDERS_BY_TRADING_PAIR_GAS_LIMIT)

    /**
     * Calculates the gas limit (in WEI) times the user-specified gas price (also in WEI).
     *
     * **NOTE**: Gas Price is internalized to a setting in this class.
     *
     * @return A [BigInteger] representing the total transaction cost, in WEI
     */
    fun getTotalTransactionCost(gasLimit: BigInteger) = gasLimit * convertGasPriceToWei

    // MARK - Private Methods

    private fun getValue(key: String, defaultValue: String): BigInteger {
        return BigInteger(looprSettings.getString(key) ?: defaultValue)
    }

}