package org.loopring.looprwallet.core.networking.eth

import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.utilities.BuildUtility
import org.loopring.looprwalletnetwork.models.loopring.responseObjects.LooprOrderItem
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigInteger

/**
 * Created by corey on 5/12/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
interface LoopringSmartContractService {

    companion object {

        fun getInstance(): LoopringSmartContractService {
            val environment = BuildUtility.BUILD_FLAVOR
            return when (environment) {
                BuildUtility.FLAVOR_MOCKNET -> LoopringSmartContractServiceMockImpl()
                BuildUtility.FLAVOR_TESTNET, BuildUtility.FLAVOR_MAINNET -> LoopringSmartContractServiceProdImpl()
                else -> throw IllegalArgumentException("Invalid environment, found: $environment")
            }
        }
    }


    /**
     * Cancels all of the provided [wallet]'s open orders.
     *
     * @param wallet The wallet of the user whose orders will be cancelled
     * @param gasLimit The gas limit of the tx, in WEI
     * @param gasPrice The gas price of the tx, in GWEI
     */
    fun cancelAllOrders(wallet: LooprWallet, gasLimit: BigInteger, gasPrice: BigInteger): Deferred<TransactionReceipt>

    /**
     * Cancels all of the provided [wallet]'s open orders, by trading pair.
     *
     * @param market The market of the orders that should be cancelled. For example: *LRC-WETH*.
     * @param wallet The wallet of the user whose orders will be cancelled
     * @param gasLimit The gas limit of the tx, in WEI
     * @param gasPrice The gas price of the tx, in GWEI
     */
    fun cancelAllOrdersByTradingPair(
            market: String,
            wallet: LooprWallet,
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ): Deferred<TransactionReceipt>

    /**
     * Cancels the provided [wallet]'s order.
     *
     * @param order The order that will be cancelled
     * @param wallet The wallet of the user whose orders will be cancelled
     * @param gasLimit The gas limit of the tx, in WEI
     * @param gasPrice The gas price of the tx, in GWEI
     */
    fun cancelOrder(
            order: LooprOrderItem,
            wallet: LooprWallet,
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ): Deferred<TransactionReceipt>

}