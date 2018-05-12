package org.loopring.looprwallet.core.viewmodels.eth

import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.networking.eth.EthereumService
import org.loopring.looprwallet.core.viewmodels.TransactionViewModel
import org.web3j.protocol.core.methods.response.TransactionReceipt
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
     * Sends ether from the sender to the recipient.
     *
     * This method runs on the [IO] context's thread.
     *
     * @param recipient The recipient's address
     * @param amount The amount of Ether to send to the recipient (represented as a whole number).
     * For example, a value passed of 1.0 will send 1.0 ETH
     * @param gasLimit The gas limit for sending this amount of Ether.
     * @param gasPrice The price (in Gwei) for sending the Ether.
     */
    fun sendEther(
            wallet: LooprWallet,
            recipient: String,
            amount: BigInteger,
            gasLimit: BigInteger,
            gasPrice: BigInteger
    ) = async(IO) {
        try {
            mIsTransactionRunning.postValue(true)

            val result = EthereumService.getInstance(wallet.credentials)
                    .sendEther(recipient, amount, gasLimit, gasPrice)
                    .await()

            mResult.postValue(result)
        } catch (e: Throwable) {
            mError.postValue(e)
        } finally {
            mIsTransactionRunning.postValue(false)
        }
    }

}