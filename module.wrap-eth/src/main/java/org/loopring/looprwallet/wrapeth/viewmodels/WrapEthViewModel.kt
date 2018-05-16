package org.loopring.looprwallet.wrapeth.viewmodels

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.settings.EthereumFeeSettings
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.networking.eth.EthereumService
import org.loopring.looprwallet.core.viewmodels.TransactionViewModel
import org.loopring.looprwallet.wrapeth.dagger.wrapEthLooprComponent
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigInteger
import javax.inject.Inject

/**
 * Created by corey on 5/10/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class WrapEthViewModel : TransactionViewModel<TransactionReceipt>() {

    @Inject
    lateinit var ethereumFeeSettings: EthereumFeeSettings

    init {
        wrapEthLooprComponent.inject(this)
    }

    /**
     * Converts the given amount in ETH to WETH
     * @param amount The amount of WETH/ETH to convert, formatted as a big number with 18 decimal
     * places
     */
    fun convertToWrapped(amount: BigInteger, wallet: LooprWallet) {
        performTransaction {
            val contractAddress = LooprToken.WETH.identifier
            val gasPrice = ethereumFeeSettings.gasPriceInGwei
            val gasLimit = ethereumFeeSettings.wethDepositGasLimit

            val service = EthereumService.getInstance(wallet.credentials)
            return@performTransaction service.depositWeth(contractAddress, wallet.credentials, amount, gasLimit, gasPrice)
        }
    }

    /**
     * Converts the given amount in WETH to ETH
     * @param amount The amount of WETH/ETH to convert, formatted as a big number with 18 decimal
     * places
     */
    fun convertToEther(amount: BigInteger, wallet: LooprWallet) {
        performTransaction {
            val contractAddress = LooprToken.WETH.identifier
            val gasPrice = ethereumFeeSettings.gasPriceInGwei
            val gasLimit = ethereumFeeSettings.wethDepositGasLimit

            val service = EthereumService.getInstance(wallet.credentials)
            return@performTransaction service.depositWeth(contractAddress, wallet.credentials, amount, gasLimit, gasPrice)
        }
    }

    // MARK - Private Methods

    private fun performTransaction(transaction: () -> Deferred<TransactionReceipt>): Deferred<Unit> = async(IO) {
        mIsTransactionRunning.postValue(true)
        try {
            val receipt = transaction().await()
            mResult.postValue(receipt)
        } catch (e: Exception) {
            mError.postValue(e)
        } finally {
            mIsTransactionRunning.postValue(false)
        }
    }

}