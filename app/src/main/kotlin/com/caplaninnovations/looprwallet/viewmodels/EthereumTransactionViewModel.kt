package com.caplaninnovations.looprwallet.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import org.web3j.crypto.Credentials
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.Transfer
import org.web3j.utils.Convert
import java.math.BigDecimal

/**
 * Created by Corey Caplan on 2/20/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To send updates to the main thread for ETH transactions
 *
 */
class dEthereumTransactionViewModel : ViewModel() {

    val isTransactionRunning = MutableLiveData<Boolean>()

    val transactionResult = MutableLiveData<TransactionReceipt>()

    val transactionError = MutableLiveData<Throwable>()

    fun sendEthereum(credentials: Credentials, recipient: String, amount: BigDecimal) {
        isTransactionRunning.value = true

        val ethereumClient = LooprWalletApp.application.ethereumClient
        Transfer.sendFunds(ethereumClient, credentials, recipient, amount, Convert.Unit.ETHER)
                .observable()
                .subscribe(this::onEthereumSendSuccess, this::onEthereumSendFailure)
    }

    // MARK - Private Methods

    private fun onEthereumSendSuccess(receipt: TransactionReceipt) {
        isTransactionRunning.value = false
        transactionResult.value = receipt
    }

    private fun onEthereumSendFailure(e: Throwable) {
        isTransactionRunning.value = false
        transactionError.value = e
    }

}