package org.loopring.looprwallet.core.networking.loopr

import io.realm.RealmList
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.loopring.looprwallet.core.models.markets.TradingPair
import org.loopring.looprwallet.core.models.order.LooprOrder
import org.loopring.looprwallet.core.models.order.LooprOrderFill
import org.loopring.looprwallet.core.models.order.OrderFilter
import org.loopring.looprwallet.core.networking.loopr.LooprMarketsServiceMockImpl.Companion.lrcTradingPair
import org.loopring.looprwallet.core.networking.loopr.LooprMarketsServiceMockImpl.Companion.reqTradingPair
import org.loopring.looprwallet.core.networking.loopr.LooprMarketsServiceMockImpl.Companion.zrxTradingPair
import org.loopring.looprwallet.core.utilities.NetworkUtility
import java.io.IOException
import java.util.*

/**
 * Created by Corey on 4/24/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
internal class LooprOrderServiceMockImpl : LooprOrderService {

    private val order1 = LooprOrder(orderHash = "abcdef1234", tradingPair = lrcTradingPair, percentageFilled = 0)
    private val order2 = LooprOrder(orderHash = "abcdef4321", tradingPair = reqTradingPair, percentageFilled = 78, status = OrderFilter.FILTER_OPEN_PARTIAL)
    private val order3 = LooprOrder(orderHash = "abcdef4132", tradingPair = zrxTradingPair, percentageFilled = 45, status = OrderFilter.FILTER_OPEN_PARTIAL)

    private val fill1 = LooprOrderFill("abc", order1.orderHash, 143.00, Date(Date().time - 100000L))
    private val fill2 = LooprOrderFill("def", order1.orderHash, 2045.00, Date(Date().time - 70000L))
    private val fill3 = LooprOrderFill("acd", order1.orderHash, 1043.00, Date(Date().time - 30000L))

    override fun getOrdersByAddress(address: String): Deferred<RealmList<LooprOrder>> = async {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            return@async RealmList(order1, order2, order3)
        } else {
            throw IOException("No connection")
        }
    }

    override fun getOrderByHash(orderHash: String): Deferred<LooprOrder> = async {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            return@async LooprOrder(tradingPair = lrcTradingPair, percentageFilled = 0)
        } else {
            throw IOException("No connection")
        }
    }

    override fun getOrderFillsByOrderHash(orderHash: String): Deferred<RealmList<LooprOrderFill>> = async {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            return@async RealmList(fill1, fill2, fill3)
        } else {
            throw IOException("No connection")
        }
    }
}