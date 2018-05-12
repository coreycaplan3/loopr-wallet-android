package org.loopring.looprwallet.core.networking.eth

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.loopring.looprwallet.core.models.android.architecture.NET
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.utilities.NetworkUtility
import org.loopring.looprwalletnetwork.models.loopring.responseObjects.LooprOrderItem
import java.io.IOException
import java.math.BigInteger

/**
 * Created by corey on 5/12/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class LoopringSmartContractServiceMockImpl : LoopringSmartContractService {

    override fun cancelAllOrders(wallet: LooprWallet, gasLimit: BigInteger, gasPrice: BigInteger) = async(NET) {

        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        return@async when {
            NetworkUtility.isNetworkAvailable() -> EthereumServiceMockImpl.mockTransactionReceipt
            else -> throw IOException("No connection!")
        }
    }

    override fun cancelAllOrdersByTradingPair(market: String, wallet: LooprWallet, gasLimit: BigInteger, gasPrice: BigInteger) = async(NET) {

        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        return@async when {
            NetworkUtility.isNetworkAvailable() -> EthereumServiceMockImpl.mockTransactionReceipt
            else -> throw IOException("No connection!")
        }
    }

    override fun cancelOrder(order: LooprOrderItem, wallet: LooprWallet, gasLimit: BigInteger, gasPrice: BigInteger) = async(NET) {

        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        return@async when {
            NetworkUtility.isNetworkAvailable() -> EthereumServiceMockImpl.mockTransactionReceipt
            else -> throw IOException("No connection!")
        }
    }

}