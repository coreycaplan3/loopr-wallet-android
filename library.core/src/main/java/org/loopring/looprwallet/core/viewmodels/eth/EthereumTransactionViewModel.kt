package org.loopring.looprwallet.core.viewmodels.eth

import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.networking.eth.EthereumService
import org.loopring.looprwallet.core.viewmodels.TransactionViewModel
import org.web3j.crypto.Credentials
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Created by Corey Caplan on 2/20/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To send updates to the main thread for ETH transactions
 *
 */
class EthereumTransactionViewModel : TransactionViewModel<TransactionReceipt>() {

    /**
     * Sends ether from the sender to the recipient. This method is asynchronous and uses the
     * callbacks to communicate back to the caller.
     *
     * @see EthereumService.sendEther
     */
    fun sendEther(
            credentials: Credentials,
            recipient: String,
            amount: BigDecimal,
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ) = async<Unit> {
        try {
            mIsTransactionRunning.postValue(true)
            EthereumService.getInstance(credentials).sendEther(recipient, amount, gasLimit, gasPrice)
        } catch (e: Throwable) {
            mError.postValue(e)
        } finally {
            mIsTransactionRunning.postValue(false)
        }
    }

    /**
     * Sends tokens from [credentials] to the [recipient]. This method is asynchronous.
     *
     * @see EthereumService.sendToken
     */
    fun sendTokens(
            contractAddress: String,
            binary: String,
            credentials: Credentials,
            recipient: String,
            amount: BigInteger,
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ) = async<Unit> {
        mIsTransactionRunning.postValue(true)
        try {
            EthereumService.getInstance(credentials)
                    .sendToken(contractAddress, binary, recipient, amount, gasLimit, gasPrice)
                    .await()
        } catch (e: Throwable) {
            mError.postValue(e)
        } finally {
            mIsTransactionRunning.postValue(false)
        }
    }

    /**
     * **ERC-20 Function**
     * Approves a given [spender] to use [amount] tokens on behalf of user ([credentials]).
     *
     * @see EthereumService.approveToken
     */
    fun approveToken(
            contractAddress: String,
            binary: String,
            credentials: Credentials,
            spender: String,
            amount: BigInteger,
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ) = async<Unit> {
        mIsTransactionRunning.postValue(true)
        try {
            EthereumService.getInstance(credentials)
                    .approveToken(contractAddress, binary, credentials, spender, amount, gasLimit, gasPrice)
                    .await()
        } catch (e: Throwable) {
            mError.postValue(e)
        } finally {
            mIsTransactionRunning.postValue(false)
        }
    }

}