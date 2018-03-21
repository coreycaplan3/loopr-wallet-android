package com.caplaninnovations.looprwallet.networking.eth

import com.caplaninnovations.looprwallet.BuildConfig
import kotlinx.coroutines.experimental.Deferred
import org.web3j.crypto.Credentials
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigDecimal

/**
 * Created by Corey Caplan on 3/10/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To interact with the Ethereum network to perform Ether (the gas/currency)
 * related transactions.
 */
interface EtherService {

    companion object {

        /**
         * Gets an instance of the transaction repository for executing ether transactions
         */
        fun getInstance(credentials: Credentials): EtherService {
            val environment = BuildConfig.ENVIRONMENT
            return when (environment) {
                "mocknet" -> EtherServiceMockImpl()
                "testnet", "mainnet" -> EtherServiceProdImpl(credentials)
                else -> throw IllegalArgumentException("Invalid environment, found: $environment")
            }
        }
    }

    /**
     * Sends ether from the sender to the recipient. This method does **NOT** block.
     *
     * @param recipient The recipient's address
     * @param amount The amount of Ether to send to the recipient (represented as a whole number).
     * For example, a value passed of 1.0 will send 1.0 ETH)
     */
    fun sendEther(recipient: String, amount: BigDecimal): Deferred<TransactionReceipt>

}