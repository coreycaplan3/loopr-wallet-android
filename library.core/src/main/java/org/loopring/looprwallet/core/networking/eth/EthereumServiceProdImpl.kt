package org.loopring.looprwallet.core.networking.eth

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
 * Purpose of Class: An implementation of [EthereumService] that communicates with the
 * Ethereum network. The type of network (testnet vs. mainnet) with which it interacts depends on
 * the build flavor. This can be checked easily by examining the value passed in [BuildConfig.ENVIRONMENT]
 */
class EthereumServiceProdImpl(private val credentials: Credentials) : EthereumService {

    private val ethereumClient = LooprWalletApp.application.ethereumClient

    override fun sendEther(recipient: String, amount: BigDecimal, gasLimit: BigDecimal, gasPrice: BigDecimal): Deferred<TransactionReceipt> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendToken(contractAddress: String, recipient: String, amount: BigDecimal, gasLimit: BigDecimal, gasPrice: BigDecimal): Deferred<TransactionReceipt> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}