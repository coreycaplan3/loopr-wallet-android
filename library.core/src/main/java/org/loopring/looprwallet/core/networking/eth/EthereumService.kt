package org.loopring.looprwallet.core.networking.eth

import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.dagger.coreLooprComponent
import org.loopring.looprwallet.core.utilities.BuildUtility
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_MAINNET
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_MOCKNET
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_TESTNET
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigInteger
import javax.inject.Inject

/**
 * Created by Corey Caplan on 3/10/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To interact with the Ethereum network to perform ETH related transactions,
 * like sending tokens and Ether.
 */
abstract class EthereumService {

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

    @Inject
    lateinit var web3j: Web3j

    init {
        injectComponents()
    }

    /**
     * A final function used for not leaving "this" from a non-final context
     */
    private fun injectComponents() {
        coreLooprComponent.inject(this)
    }

    /**
     * Sends ether from the sender to the recipient. This method runs on the NET context's thread.
     *
     * @param recipient The recipient's address
     * @param amount The amount of Ether to send to the recipient (represented as a big number,
     * using 10^18 zeros as padding, as per ETH's standard).
     * @param gasLimit The gas limit for sending this amount of ETH.
     * @param gasPrice The price (in Gwei) for sending the ETH.
     */
    abstract fun sendEther(recipient: String,
                           amount: BigInteger,
                           gasLimit: BigInteger,
                           gasPrice: BigInteger
    ): Deferred<TransactionReceipt>


    /**
     * Sends tokens from the sender to the recipient. This method runs on the NET context's thread.
     *
     * @param contractAddress The address of the token's contract.
     * @param recipient The recipient's address
     * @param amount The amount of tokens to send to the recipient. This number is represented as a
     * [BigInteger] with the token's number of decimal places as a padding for extra zeros.
     * @param gasLimit The gas limit for sending this amount of tokens, in WEI.
     * @param gasPrice The gas price for sending the tokens, in GWEI
     */
    abstract fun sendToken(contractAddress: String,
                           recipient: String,
                           amount: BigInteger,
                           gasLimit: BigInteger,
                           gasPrice: BigInteger
    ): Deferred<TransactionReceipt>

    /**
     * **ERC-20 Function**
     * Approves a given [spender] to use [amount] tokens on behalf of user ([credentials]).
     *
     * **THIS FUNCTION IS REQUIRED FOR LOOPRING TO TRADE ON BEHALF OF THE USER, ONCE AN ORDER IS
     * FOUND**
     *
     * @param contractAddress The address of the token's contract
     * @param credentials The [Credentials] of the user whose tokens will be spendable by the
     * Loopring smart contract
     * @param amount The amount of tokens that the LoopringTransferDelegate will be able to spend.
     * This number is represented as a [BigInteger] with the token's number of decimal places as a
     * padding for extra zeros.
     * @param gasLimit The amount of gas needed to perform the approval, in WEI.
     * @param gasPrice The price for the transaction, in GWEI
     */
    abstract fun approveToken(
            contractAddress: String,
            credentials: Credentials,
            spender: String,
            amount: BigInteger,
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ): Deferred<TransactionReceipt>

    /**
     * ** WETH Function**
     * Deposits the given [amount] from ETH into WETH
     *
     * @param contractAddress The address of WETH's contract
     * @param credentials The [Credentials] of the user who will deposit ETH
     * @param amount The amount of ETH to deposit, in WEI
     * @param gasLimit The amount of gas needed to perform the function, in WEI.
     * @param gasPrice The price for the transaction, in GWEI
     */
    abstract fun depositWeth(
            contractAddress: String,
            credentials: Credentials,
            amount: BigInteger,
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ): Deferred<TransactionReceipt>

    /**
     * ** WETH Function**
     * Withdraws the given [amount] from WETH into ETH
     *
     * @param contractAddress The address of WETH's contract
     * @param credentials The [Credentials] of the user who will withdraw ETH
     * @param amount The amount of WETH to withdraw, in WEI
     * @param gasLimit The amount of gas needed to perform the function, in WEI.
     * @param gasPrice The price for the transaction, in GWEI
     */
    abstract fun withdrawWeth(
            contractAddress: String,
            credentials: Credentials,
            amount: BigInteger,
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ): Deferred<TransactionReceipt>

}