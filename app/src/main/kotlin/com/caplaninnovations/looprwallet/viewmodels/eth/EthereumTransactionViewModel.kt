package com.caplaninnovations.looprwallet.viewmodels.eth

import com.caplaninnovations.looprwallet.networking.eth.EtherService
import com.caplaninnovations.looprwallet.viewmodels.TransactionViewModel
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
     * @param recipient The recipient's address
     * @param amount The amount of Ether to send to the recipient (represented as a whole number of
     * Ether. Meaning, a value passed of 1.0 will send 1.0 ether)
     * @see EtherService.sendEther
     */
    fun sendEthereum(credentials: Credentials, recipient: String, amount: BigDecimal) {
        isTransactionRunning.value = true

        // TODO
        EtherService.getInstance(credentials)
                .sendEther(recipient, amount)
    }

    // MARK - Private Methods

    private fun onEthereumSendSuccess(receipt: TransactionReceipt) {
        isTransactionRunning.value = false
        result.value = receipt
    }

    private fun onEthereumSendFailure(e: Throwable) {
        isTransactionRunning.value = false
        error.value = e
    }

}