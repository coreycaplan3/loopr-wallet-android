package com.caplaninnovations.looprwallet.viewmodels.eth

import com.caplaninnovations.looprwallet.networking.eth.EthereumService
import com.caplaninnovations.looprwallet.viewmodels.TransactionViewModel
import kotlinx.coroutines.experimental.async
import org.web3j.crypto.Credentials
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigDecimal

/**
 * Created by Corey Caplan on 2/20/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To send updates to the main thread for ETH transactions
 *
 */
class EthereumTransactionViewModel : TransactionViewModel<TransactionReceipt>() {

    /**
     * Sends ether from the sender to the recipient. This method is asynchronous and uses the
     * callbacks to communicate back to the caller.
     *
     * @see EthereumService.sendEther
     */
    fun sendEther(
            credentials: Credentials,
            recipient: String,
            amount: BigDecimal,
            gasLimit: BigDecimal,
            gasPrice: BigDecimal
    ) = async<Unit> {
        try {
            isTransactionRunning.postValue(true)
            EthereumService.getInstance(credentials).sendEther(recipient, amount, gasLimit, gasPrice)
        } catch (e: Throwable) {
            error.postValue(e)
        } finally {
            isTransactionRunning.postValue(false)
        }
    }

    /**
     * Sends ether from the sender to the recipient. This method is asynchronous and uses the
     * callbacks to communicate back to the caller.
     *
     * @see EthereumService.sendEther
     */
    fun sendTokens(
            contractAddress: String,
            credentials: Credentials,
            recipient: String,
            amount: BigDecimal,
            gasLimit: BigDecimal,
            gasPrice: BigDecimal
    ) = async<Unit> {
        isTransactionRunning.postValue(true)
        try {
            EthereumService.getInstance(credentials)
                    .sendToken(contractAddress, recipient, amount, gasLimit, gasPrice)
                    .await()
        } catch (e: Throwable) {
            error.postValue(e)
        } finally {
            isTransactionRunning.postValue(false)
        }
    }

}