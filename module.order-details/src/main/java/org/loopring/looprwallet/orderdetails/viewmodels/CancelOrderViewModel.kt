package org.loopring.looprwallet.orderdetails.viewmodels

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.loopr.orders.AppLooprOrder
import org.loopring.looprwallet.core.models.settings.EthereumFeeSettings
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.networking.eth.LoopringSmartContractService
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

    fun cancelOrder(wallet: LooprWallet, order: AppLooprOrder) = runTaskAsync { gasPrice ->
        val gasLimit = ethereumFeeSettings.cancelOrderGasLimit
        return@runTaskAsync service.cancelOrder(AppLooprOrder.convertToLibraryLooprOrder(order), wallet, gasLimit, gasPrice)
    }

    fun cancelOrderByTradingPair(wallet: LooprWallet, market: String) = runTaskAsync { gasPrice ->
        val gasLimit = ethereumFeeSettings.cancelOrdersByTradingPair
        return@runTaskAsync service.cancelAllOrdersByTradingPair(market, wallet, gasLimit, gasPrice)
    }

    fun cancelAllOrders(wallet: LooprWallet) = runTaskAsync { gasPrice ->
        val gasLimit = ethereumFeeSettings.cancelAllOrdersGasLimit
        return@runTaskAsync service.cancelAllOrders(wallet, gasLimit, gasPrice)
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