package org.loopring.looprwallet.core.networking.eth

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.loopring.looprwallet.core.models.android.architecture.NET
import org.loopring.looprwallet.core.utilities.NetworkUtility
import org.loopring.looprwalletnetwork.models.ethereum.EthBlockNum
import java.io.IOException
import java.math.BigInteger

/**
 * Created by Corey on 4/24/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 */
internal class EthereumGenericServiceMockImpl : EthereumGenericService() {

    /**
     * 5 million
     */
    private val ethereumBlockNumber = "5000000"

    override fun getBlockNumber(): Deferred<EthBlockNum> = async(NET) {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            return@async EthBlockNum().apply {
                blockNumber = BigInteger(ethereumBlockNumber)
            }
        } else {
            throw IOException("No connection")
        }
    }

}