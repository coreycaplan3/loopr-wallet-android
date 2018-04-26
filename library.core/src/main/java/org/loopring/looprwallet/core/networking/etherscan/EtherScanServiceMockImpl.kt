package org.loopring.looprwallet.core.networking.etherscan

import org.loopring.looprwallet.core.models.cryptotokens.LooprToken
import org.loopring.looprwallet.core.utilities.NetworkUtility
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.loopring.looprwallet.core.models.android.architecture.NET
import java.io.IOException

/**
 * Created by Corey Caplan on 3/31/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
internal class EtherScanServiceMockImpl : EtherScanService {

    // Get's LRC by default
    override fun getTokenBinary(contractAddress: String) = async(NET) {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            LooprToken.LRC.binary!!
        } else {
            throw IOException("No connection!")
        }
    }
}