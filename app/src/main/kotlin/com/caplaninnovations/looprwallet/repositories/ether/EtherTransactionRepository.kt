package com.caplaninnovations.looprwallet.repositories.ether

import com.caplaninnovations.looprwallet.BuildConfig
import org.web3j.crypto.Credentials
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigDecimal

/**
 * Created by Corey Caplan on 3/10/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To allow interaction between the client and the Ethereum network or to mock
 * the calls in a clean way.
 */
interface EtherTransactionRepository {

    companion object {

        /**
         * Gets an instance of the transaction repository for executing ether transactions
         */
        fun getInstance(credentials: Credentials): EtherTransactionRepository {
            val environment = BuildConfig.ENVIRONMENT
            return when (environment) {
                "mocknet" -> EtherTransactionRepositoryMockImpl()
                "testnet", "mainnet" -> EtherTransactionRepositoryProdImpl(credentials)
                else -> throw IllegalArgumentException("Invalid environment, found: $environment")
            }
        }
    }

    /**
     * Sends ether from the sender to the recipient. This method is asynchronous and uses the
     * callbacks to communicate back to the caller.
     *
     * @param recipient The recipient's address
     * @param amount The amount of Ether to send to the recipient (represented as a whole number of
     * Ether. Meaning, a value passed of 1.0 will send 1.0 ether)
     * @param onSuccess The callback to be invoked if the call succeeds
     * @param onFailure The callback to be invoked if the call fails
     */
    fun sendEther(
            recipient: String,
            amount: BigDecimal,
            onSuccess: (TransactionReceipt) -> Unit,
            onFailure: (Throwable) -> Unit
    )

}