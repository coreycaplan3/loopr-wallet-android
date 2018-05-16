package org.loopring.looprwallet.core.networking.eth

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.models.android.architecture.NET
import org.loopring.looprwalletnetwork.services.Erc20Service
import org.loopring.looprwalletnetwork.services.SendEth
import org.web3j.crypto.Credentials
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigInteger

/**
 * Created by Corey Caplan on 3/10/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: An implementation of [EthereumService] that communicates with the
 * Ethereum network. The type of network (test-net vs. main-net) with which it interacts depends on
 * the build flavor. This can be checked easily by examining the value passed in the *BuildConfig*.
 */
internal class EthereumServiceProdImpl(private val credentials: Credentials) : EthereumService() {

    override fun sendEther(
            recipient: String,
            amount: BigInteger,
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ): Deferred<TransactionReceipt> = async(NET) {
        SendEth.getService(web3j)
                .sendEther(recipient, amount.toBigDecimal(), credentials, gasPrice, gasLimit)
                .await()
    }

    override fun sendToken(
            contractAddress: String,
            recipient: String,
            amount: BigInteger,
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ): Deferred<TransactionReceipt> = async(NET) {
        Erc20Service.getService(contractAddress, web3j, credentials, gasPrice, gasLimit, "")
                .transfer(recipient, amount)
                .await()
    }

    override fun approveToken(
            contractAddress: String,
            credentials: Credentials,
            spender: String,
            amount: BigInteger,
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ): Deferred<TransactionReceipt> = async(NET) {
        Erc20Service.getService(contractAddress, web3j, credentials, gasPrice, gasLimit, "")
                .approve(spender, amount)
                .await()
    }

    override fun depositWeth(
            contractAddress: String,
            credentials: Credentials,
            amount: BigInteger,
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ): Deferred<TransactionReceipt> {
        TODO("not implemented")
    }

    override fun withdrawWeth(
            contractAddress: String,
            credentials: Credentials,
            amount: BigInteger,
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ): Deferred<TransactionReceipt> {
        TODO("not implemented")
    }
}