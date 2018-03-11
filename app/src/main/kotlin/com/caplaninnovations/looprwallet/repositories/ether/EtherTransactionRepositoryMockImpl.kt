package com.caplaninnovations.looprwallet.repositories.ether

import com.caplaninnovations.looprwallet.utilities.isNetworkAvailable
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.io.IOException
import java.math.BigDecimal

/**
 * Created by Corey Caplan on 3/10/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class EtherTransactionRepositoryMockImpl : EtherTransactionRepository {

    override fun sendEther(
            recipient: String,
            amount: BigDecimal,
            onSuccess: (TransactionReceipt) -> Unit,
            onFailure: (Throwable) -> Unit
    ) {
        async {

            // Make a fake delay of 500ms
            delay(500L)

            if (isNetworkAvailable()) {
                onSuccess(TransactionReceipt())
            } else {
                onFailure(IOException("No connection!"))
            }
        }
    }
}