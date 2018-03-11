package com.caplaninnovations.looprwallet.repositories.ether

import com.caplaninnovations.looprwallet.BuildConfig
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import org.web3j.crypto.Credentials
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.Transfer
import org.web3j.utils.Convert
import java.math.BigDecimal

/**
 * Created by Corey Caplan on 3/10/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: An implementation of [EtherTransactionRepository] that communicates with the
 * Ethereum network. The type of network (testnet vs. mainnet) with which it interacts depends on
 * the build flavor. This can be checked easily by examining the value passed in [BuildConfig.ENVIRONMENT]
 */
class EtherTransactionRepositoryProdImpl(private val credentials: Credentials) : EtherTransactionRepository {

    private val ethereumClient = LooprWalletApp.application.ethereumClient

    override fun sendEther(recipient: String,
                           amount: BigDecimal,
                           onSuccess: (TransactionReceipt) -> Unit,
                           onFailure: (Throwable) -> Unit) {
        Transfer.sendFunds(ethereumClient, credentials, recipient, amount, Convert.Unit.ETHER)
                .observable()
                .subscribe(onSuccess, onFailure)

    }

}