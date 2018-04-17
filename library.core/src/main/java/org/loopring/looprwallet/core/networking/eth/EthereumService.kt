package org.loopring.looprwallet.core.networking.eth

import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.utilities.BuildUtility
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_MAINNET
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_MOCKNET
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_TESTNET
import org.web3j.crypto.Credentials
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigDecimal

/**
 * Created by Corey Caplan on 3/10/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To interact with the Ethereum network to perform Ethereum related transactions,
 * like sending tokens and Ether.
 */
interface EthereumService {

    companion object {

        /**
         * Gets an instance of the transaction repository for executing ether transactions
         */
        fun getInstance(credentials: Credentials): EthereumService {
            val environment = BuildUtility.BUILD_FLAVOR
            return when (environment) {
                FLAVOR_MOCKNET -> EthereumServiceMockImpl()
                FLAVOR_TESTNET, FLAVOR_MAINNET -> EthereumServiceProdImpl(credentials)
                else -> throw IllegalArgumentException("Invalid environment, found: $environment")
            }
        }
    }

    /**
     * Sends ether from the sender to the recipient. This method does **NOT** block.
     *
     * @param recipient The recipient's address
     * @param amount The amount of Ether to send to the recipient (represented as a whole number).
     * For example, a value passed of 1.0 will send 1.0 ETH
     * @param gasLimit The gas limit for sending this amount of Ether.
     * @param gasPrice The price (in Gwei) for sending the Ether.
     */
    fun sendEther(recipient: String,
                  amount: BigDecimal,
                  gasLimit: BigDecimal,
                  gasPrice: BigDecimal
    ): Deferred<TransactionReceipt>


    /**
     * Sends tokens from the sender to the recipient. This method does **NOT** block.
     *
     * @param contractAddress The address of the token's contract.
     * @param recipient The recipient's address
     * @param amount The amount of tokens to send to the recipient (represented as a whole number).
     * For example, a value passed of 1.0 will send 1.0 tokens
     * @param gasLimit The gas limit for sending this amount of tokens.
     * @param gasPrice The price (in Gwei) for sending the tokens.
     */
    fun sendToken(contractAddress: String,
                  recipient: String,
                  amount: BigDecimal,
                  gasLimit: BigDecimal,
                  gasPrice: BigDecimal
    ): Deferred<TransactionReceipt>

}