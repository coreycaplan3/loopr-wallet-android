package org.loopring.looprwallet.core.networking.eth

import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.wallet.LooprWallet
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
class LoopringSmartContractServiceProdImpl: LoopringSmartContractService {

    override fun cancelAllOrders(wallet: LooprWallet, gasLimit: BigInteger, gasPrice: BigInteger): Deferred<TransactionReceipt> {
        TODO("not implemented")
    }

    override fun cancelAllOrdersByTradingPair(market: String, wallet: LooprWallet, gasLimit: BigInteger, gasPrice: BigInteger): Deferred<TransactionReceipt> {
        TODO("not implemented")
    }

    override fun cancelOrder(order: LooprOrderItem, wallet: LooprWallet, gasLimit: BigInteger, gasPrice: BigInteger): Deferred<TransactionReceipt> {
        TODO("not implemented")
    }
}