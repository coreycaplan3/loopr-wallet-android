package org.loopring.looprwallet.orderdetails.viewmodels

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.loopr.orders.AppLooprOrder
import org.loopring.looprwallet.core.models.settings.EthereumFeeSettings
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.networking.eth.LoopringSmartContractService
import org.loopring.looprwallet.core.repositories.loopr.LooprOrderRepository
import org.loopring.looprwallet.core.viewmodels.TransactionViewModel
import org.loopring.looprwallet.orderdetails.dagger.orderDetailsLooprComponent
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigInteger
import javax.inject.Inject

/**
 * Created by corey on 5/12/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class CancelOrderViewModel : TransactionViewModel<TransactionReceipt>() {

    private val service: LoopringSmartContractService by lazy {
        LoopringSmartContractService.getInstance()
    }

    @Inject
    lateinit var ethereumFeeSettings: EthereumFeeSettings

    init {
        orderDetailsLooprComponent.inject(this)
    }

    var operation: Int = -1

    fun cancelOrder(wallet: LooprWallet, orderHash: String) = runTaskAsync { gasPrice ->
        val gasLimit = ethereumFeeSettings.cancelOrderGasLimit

        val repository = LooprOrderRepository()

        val order = repository.getOrderByHashNow(orderHash, IO)
                ?: throw IllegalStateException("Order was null!")

        val result = service.cancelOrder(AppLooprOrder.convertToLibraryLooprOrder(order), wallet, gasLimit, gasPrice)

        repository.cancelOrder(orderHash, IO)

        return@runTaskAsync result
    }

    fun cancelOrderByTradingPair(wallet: LooprWallet, market: String) = runTaskAsync { gasPrice ->
        val gasLimit = ethereumFeeSettings.cancelOrdersByTradingPair
        val result = service.cancelAllOrdersByTradingPair(market, wallet, gasLimit, gasPrice)

        val repository = LooprOrderRepository()
        repository.cancelOrdersByTradingPair(wallet.credentials.address, market, IO)

        return@runTaskAsync result
    }

    fun cancelAllOrders(wallet: LooprWallet) = runTaskAsync { gasPrice ->
        val gasLimit = ethereumFeeSettings.cancelAllOrdersGasLimit
        val result = service.cancelAllOrders(wallet, gasLimit, gasPrice)

        val repository = LooprOrderRepository()
        repository.cancelAllOpenOrders(wallet.credentials.address, IO)

        return@runTaskAsync result
    }

    // MARK - Private Methods

    /**
     * @param block A function that takes the gas price (in GWEI) and runs the tx.
     */
    private fun runTaskAsync(block: suspend (BigInteger) -> Deferred<TransactionReceipt>) = async(IO) {
        val gasPrice = ethereumFeeSettings.gasPriceInGwei
        try {
            mIsTransactionRunning.postValue(true)

            val receipt = block(gasPrice)
                    .await()

            mResult.postValue(receipt)
        } catch (e: Exception) {
            mError.postValue(e)
        } finally {
            mIsTransactionRunning.postValue(false)
        }
    }

}