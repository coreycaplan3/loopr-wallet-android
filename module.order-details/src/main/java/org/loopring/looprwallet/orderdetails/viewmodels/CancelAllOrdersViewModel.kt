package org.loopring.looprwallet.orderdetails.viewmodels

import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.settings.EthereumFeeSettings
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.networking.eth.LoopringSmartContractService
import org.loopring.looprwallet.core.viewmodels.TransactionViewModel
import org.loopring.looprwallet.orderdetails.dagger.orderDetailsLooprComponent
import org.web3j.protocol.core.methods.response.TransactionReceipt
import javax.inject.Inject

/**
 * Created by corey on 5/12/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class CancelAllOrdersViewModel : TransactionViewModel<TransactionReceipt>() {

    private val service: LoopringSmartContractService by lazy {
        LoopringSmartContractService.getInstance()
    }

    @Inject
    lateinit var ethereumFeeSettings: EthereumFeeSettings

    init {
        orderDetailsLooprComponent.inject(this)
    }

    fun cancelAllOrders(wallet: LooprWallet) = async(IO) {
        val gasLimit = ethereumFeeSettings.cancelAllOrdersGasLimit
        val gasPrice = ethereumFeeSettings.gasPriceInGwei

        mIsTransactionRunning.postValue(true)
        try {

            val receipt = service.cancelAllOrders(wallet, gasLimit, gasPrice)
                    .await()

            mResult.postValue(receipt)
        } catch (e: Exception) {
            mError.postValue(e)
        } finally {
            mIsTransactionRunning.postValue(false)
        }
    }

}