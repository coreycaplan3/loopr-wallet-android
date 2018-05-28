package org.loopring.looprwallet.core.viewmodels.eth

import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.networking.eth.EthereumService
import org.loopring.looprwallet.core.repositories.eth.EthTokenRepository
import org.loopring.looprwallet.core.viewmodels.TransactionViewModel
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigInteger

/**
 * Created by corey on 5/11/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class EthereumTokenTransactionViewModel : TransactionViewModel<Pair<LooprToken, TransactionReceipt>>() {

    /**
     * ** ERC 20 Function - Transfer **
     * Sends tokens from [wallet] to the [recipient].
     *
     * This method runs on the [IO] context'ss thread.
     *
     * @param token The token that's going to be sent
     * @param wallet The wallet of the user sending the tokens
     * @param recipient The user receiving the tokens
     * @param amount The amount of the token to be sent, represented as a [BigInteger] using
     * [LooprToken.decimalPlaces].
     * @param gasLimit The gas limit for the transaction in WEI
     * @param gasPrice The gas price for the transaction in GWEI
     */
    fun sendTokens(
            token: LooprToken,
            wallet: LooprWallet,
            recipient: String,
            amount: BigInteger,
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ) = async(IO) {
        try {
            mIsTransactionRunning.postValue(true)
            val result = EthereumService.getInstance(wallet.credentials)
                    .sendToken(token.identifier, recipient, amount, gasLimit, gasPrice)
                    .await()

            mResult.postValue(token to result)
        } catch (e: Throwable) {
            mError.postValue(e)
        } finally {
            mIsTransactionRunning.postValue(false)
        }
    }

    /**
     * **ERC-20 Function**
     * Approves a given [spender] to use [amount] tokens on behalf of user ([wallet]).
     *
     * @param token The token that's going to be sent
     * @param wallet The wallet of the user sending the tokens
     * @param spender The delegate that is allowed to spend the [token] on [wallet]'s behalf.
     * @param gasLimit The gas limit for the transaction
     * @param gasPrice The gas price for the transaction
     */
    fun approveToken(
            token: LooprToken,
            wallet: LooprWallet,
            spender: String,
            amount: BigInteger,
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ) = async(IO) {
        mIsTransactionRunning.postValue(true)
        try {
            val result = EthereumService.getInstance(wallet.credentials)
                    .approveToken(token.identifier, wallet.credentials, spender, amount, gasLimit, gasPrice)
                    .await()

            val repository = EthTokenRepository()
            repository.approveToken(wallet.credentials.address, token, amount, IO)

            mResult.postValue(token to result)
        } catch (e: Throwable) {
            mError.postValue(e)
        } finally {
            mIsTransactionRunning.postValue(false)
        }
    }

}