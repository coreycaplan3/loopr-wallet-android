package com.caplaninnovations.looprwallet.networking.eth

import com.caplaninnovations.looprwallet.utilities.NetworkUtility
import com.caplaninnovations.looprwallet.utilities.NetworkUtility.MOCK_SERVICE_CALL_DURATION
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
class EtherServiceMockImpl : EtherService {

    override fun sendEther(recipient: String, amount: BigDecimal) = async {

        // Make a fake delay of 500ms
        delay(MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            TransactionReceipt()
        } else {
            throw IOException("No Connection!")
        }
    }

}