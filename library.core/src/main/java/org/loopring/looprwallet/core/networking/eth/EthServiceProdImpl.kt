package org.loopring.looprwallet.core.networking.eth

import kotlinx.coroutines.experimental.Deferred
import org.web3j.crypto.Credentials
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigDecimal

/**
 * Created by Corey Caplan on 3/10/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: An implementation of [EthService] that communicates with the
 * Ethereum network. The type of network (testnet vs. mainnet) with which it interacts depends on
 * the build flavor. This can be checked easily by examining the value passed in the *BuildConfig*.
 */
internal class EthServiceProdImpl(private val credentials: Credentials) : EthService {

    override fun sendEther(
            recipient: String,
            amount: BigDecimal,
            gasLimit: BigDecimal,
            gasPrice: BigDecimal
    ): Deferred<TransactionReceipt> {
        TODO("not implemented")
    }

    override fun sendToken(
            contractAddress: String,
            recipient: String,
            amount: BigDecimal,
            gasLimit: BigDecimal,
            gasPrice: BigDecimal
    ): Deferred<TransactionReceipt> {
        TODO("not implemented")
    }

    override fun approveToken(
            contractAddress: String,
            credentials: Credentials,
            spender: String,
            amount: BigDecimal,
            gasLimit: BigDecimal,
            gasPrice: BigDecimal
    ): Deferred<TransactionReceipt> {
        TODO("not implemented")
    }
}