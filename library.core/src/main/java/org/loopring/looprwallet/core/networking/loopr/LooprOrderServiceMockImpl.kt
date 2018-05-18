package org.loopring.looprwallet.core.networking.loopr

import android.util.Base64
import io.realm.RealmList
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.loopring.looprwallet.core.models.android.architecture.NET
import org.loopring.looprwallet.core.models.loopr.orders.*
import org.loopring.looprwallet.core.models.loopr.paging.LooprPagingItem
import org.loopring.looprwallet.core.networking.loopr.LooprMarketsServiceMockImpl.Companion.lrcTradingPair
import org.loopring.looprwallet.core.networking.loopr.LooprMarketsServiceMockImpl.Companion.reqTradingPair
import org.loopring.looprwallet.core.networking.loopr.LooprMarketsServiceMockImpl.Companion.zrxTradingPair
import org.loopring.looprwallet.core.utilities.NetworkUtility
import java.io.IOException
import java.security.MessageDigest
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

    companion object {

        private fun createHash(): String {
            val digest = MessageDigest.getInstance("SHA-256")
                    .digest("hello".toByteArray())
            return String(Base64.encode(digest, Base64.DEFAULT))
        }

    }

    private val order1 = AppLooprOrder(orderHash = createHash(), tradingPair = lrcTradingPair, percentageFilled = 0, status = OrderSummaryFilter.FILTER_OPEN_NEW)
    private val order2 = AppLooprOrder(orderHash = createHash(), tradingPair = reqTradingPair, percentageFilled = 78, status = OrderSummaryFilter.FILTER_OPEN_PARTIAL)
    private val order3 = AppLooprOrder(orderHash = createHash(), tradingPair = zrxTradingPair, percentageFilled = 45, status = OrderSummaryFilter.FILTER_OPEN_PARTIAL, isSell = true)

    private val fill1 = LooprOrderFill("abc", order1.orderHash, 143.00, Date(Date().time - 100000L))
    private val fill2 = LooprOrderFill("def", order1.orderHash, 2045.00, Date(Date().time - 70000L))
    private val fill3 = LooprOrderFill("acd", order1.orderHash, 1043.00, Date(Date().time - 30000L))

    override fun submitOrder(looprOrder: AppLooprOrder): Deferred<Unit> = async(NET) {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            return@async
        } else {
            throw IOException("No connection")
        }
    }

    override fun getOrdersByAddress(orderFilter: OrderSummaryFilter): Deferred<LooprOrderContainer> = async(NET) {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            val criteria = LooprOrderContainer.createCriteria(orderFilter)
            val data = RealmList(order1, order2, order3)
            val pagingItems = RealmList<LooprPagingItem>(LooprPagingItem(criteria, orderFilter.pageNumber, data.size * 2))
            return@async LooprOrderContainer(criteria = criteria, pagingItems = pagingItems, data = data)
        } else {
            throw IOException("No connection")
        }
    }

    override fun getOrderByHash(orderHash: String): Deferred<AppLooprOrder> = async(NET) {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            return@async RealmList(order1, order2, order3).find { it.orderHash == orderHash }!!
        } else {
            throw IOException("No connection")
        }
    }

    override fun getOrderFillsByOrderHash(orderFillFilter: OrderFillFilter): Deferred<LooprOrderFillContainer> = async(NET) {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            val list = listOf(fill1, fill2, fill3).map {
                it.orderHash = orderFillFilter.orderHash
                return@map it
            }
            val realmList = RealmList<LooprOrderFill>().apply { addAll(list) }

            val criteria = LooprOrderFillContainer.createCriteria(orderFillFilter)
            val pagingItems = RealmList<LooprPagingItem>(LooprPagingItem(criteria, orderFillFilter.pageNumber, list.size * 3))
            return@async LooprOrderFillContainer(criteria, OrderFillFilter.ITEMS_PER_PAGE, pagingItems, realmList)
        } else {
            throw IOException("No connection")
        }
    }
}