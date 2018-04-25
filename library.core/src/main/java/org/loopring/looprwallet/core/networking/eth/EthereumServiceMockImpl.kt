package org.loopring.looprwallet.core.networking.eth

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.loopring.looprwallet.core.models.blockchain.EthereumBlockNumber
import org.loopring.looprwallet.core.utilities.NetworkUtility
import java.io.IOException
import java.math.BigInteger

/**
 * Created by Corey on 4/24/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
internal class EthereumServiceMockImpl : EthereumService {

    companion object {

        /**
         * 5 million
         */
        const val ETHEREUM_BLOCK_NUMBER = "5000000"

    }

    override fun getBlockNumber(): Deferred<EthereumBlockNumber> = async {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            return@async EthereumBlockNumber(BigInteger(ETHEREUM_BLOCK_NUMBER))
        } else {
            throw IOException("No connection")
        }
    }

}