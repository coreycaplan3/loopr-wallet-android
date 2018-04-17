package org.loopring.looprwallet.core.networking.etherscan

import org.loopring.looprwallet.core.models.cryptotokens.EthToken
import org.loopring.looprwallet.core.utilities.NetworkUtility
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
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
    override fun getTokenBinary(contractAddress: String) = async {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            EthToken.LRC.binary!!
        } else {
            throw IOException("No connection!")
        }
    }
}