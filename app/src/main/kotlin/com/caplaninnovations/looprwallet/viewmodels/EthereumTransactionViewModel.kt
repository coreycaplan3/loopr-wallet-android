package com.caplaninnovations.looprwallet.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import org.web3j.crypto.Credentials
import org.web3j.tx.Transfer
import org.web3j.utils.Convert
import java.math.BigDecimal

/**
 * Created by Corey Caplan on 2/20/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To send updates to the main thread on ETH transactions
 *
 */
class EthereumTransactionViewModel : ViewModel() {

    private val isTransactionRunning: LiveData<Boolean> = MutableLiveData<Boolean>()

    fun sendEthereum(credentials: Credentials, recipient: String, amount: BigDecimal) {
        val ethereumClient = LooprWalletApp.application.ethereumClient
        val future = Transfer.sendFunds(ethereumClient, credentials, recipient, amount, Convert.Unit.ETHER)
                .sendAsync()
    }

}