package com.caplaninnovations.looprwallet.networking.eth

import com.caplaninnovations.looprwallet.BuildConfig
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import kotlinx.coroutines.experimental.Deferred
import org.web3j.crypto.Credentials
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigDecimal

/**
 * Created by Corey Caplan on 3/10/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: An implementation of [EtherService] that communicates with the
 * Ethereum network. The type of network (testnet vs. mainnet) with which it interacts depends on
 * the build flavor. This can be checked easily by examining the value passed in [BuildConfig.ENVIRONMENT]
 */
class EtherServiceProdImpl(private val credentials: Credentials) : EtherService {

    private val ethereumClient = LooprWalletApp.application.ethereumClient

    override fun sendEther(recipient: String,
                           amount: BigDecimal): Deferred<TransactionReceipt> {

        TODO()
    }
}