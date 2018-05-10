package org.loopring.looprwallet.core.networking.eth

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.models.android.architecture.NET
import org.loopring.looprwalletnetwork.services.Erc20Service
import org.loopring.looprwalletnetwork.services.EthService
import org.web3j.crypto.Credentials
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Created by Corey Caplan on 3/10/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: An implementation of [EthereumService] that communicates with the
 * Ethereum network. The type of network (testnet vs. mainnet) with which it interacts depends on
 * the build flavor. This can be checked easily by examining the value passed in the *BuildConfig*.
 */
internal class EthereumServiceProdImpl(private val credentials: Credentials) : EthereumService() {

    override fun sendEther(
            recipient: String,
            amount: BigDecimal,
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ): Deferred<TransactionReceipt> = async(NET) {
        EthService.getService(web3j)
                .sendEth(amount, recipient, credentials, gasPrice, gasLimit)
                .await()
                .send()
                .send()
    }

    override fun sendToken(
            contractAddress: String,
            binary: String,
            recipient: String,
            amount: BigInteger,
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ): Deferred<TransactionReceipt> = async(NET) {
        Erc20Service.getInstance(contractAddress, web3j, credentials, gasPrice, gasLimit, binary)
                .transfer(recipient, amount)
                .await()
    }

    override fun approveToken(
            contractAddress: String,
            binary: String,
            credentials: Credentials,
            spender: String,
            amount: BigInteger,
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ): Deferred<TransactionReceipt> = async(NET) {
        Erc20Service.getInstance(contractAddress, web3j, credentials, gasPrice, gasLimit, binary)
                .approve(spender, amount)
                .await()
    }
}