package org.loopring.looprwallet.core.networking.loopr

import io.realm.RealmList
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.loopring.looprwallet.core.models.android.architecture.NET
import org.loopring.looprwallet.core.models.loopr.orders.*
import org.loopring.looprwallet.core.models.loopr.paging.LooprPagingItem
import org.loopring.looprwallet.core.networking.loopr.LooprMarketsServiceMockImpl.Companion.appcTradingPair
import org.loopring.looprwallet.core.networking.loopr.LooprMarketsServiceMockImpl.Companion.lrcTradingPair
import org.loopring.looprwallet.core.networking.loopr.LooprMarketsServiceMockImpl.Companion.reqTradingPair
import org.loopring.looprwallet.core.networking.loopr.LooprMarketsServiceMockImpl.Companion.zrxTradingPair
import org.loopring.looprwallet.core.repositories.loopr.LooprOrderRepository
import org.loopring.looprwallet.core.utilities.NetworkUtility
import org.web3j.crypto.Credentials
import java.io.IOException
import java.math.BigDecimal
import java.math.BigInteger
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
            val c = Credentials.create("0000000000000000000000000000000000000000000000000000000000000000")
            return c.ecKeyPair.sign("${Math.random()}".toByteArray()).r.toString()
        }

        private val order1 = AppLooprOrder().apply {
            this.orderHash = createHash()
            this.tradingPair = lrcTradingPair
            this.percentageFilled = 0
            this.status = OrderSummaryFilter.FILTER_OPEN_NEW
            this.amount = BigInteger("343289") * (BigInteger.TEN.pow(tradingPair.primaryToken.decimalPlaces))
            this.priceInSecondaryTicker = BigDecimal("0.0015912")
            this.isSell = false // You only buy LRC and never sell --> HODL!
        }
        private val order2 = AppLooprOrder().apply {
            this.orderHash = createHash()
            this.tradingPair = reqTradingPair
            this.percentageFilled = 78
            this.status = OrderSummaryFilter.FILTER_OPEN_PARTIAL
            this.amount = BigInteger("7013") * (BigInteger.TEN.pow(tradingPair.primaryToken.decimalPlaces))
            this.priceInSecondaryTicker = BigDecimal("0.0010132")
            this.isSell = false
        }
        private val order3 = AppLooprOrder().apply {
            AppLooprOrder(orderHash = createHash(), tradingPair = zrxTradingPair, percentageFilled = 45, status = OrderSummaryFilter.FILTER_OPEN_PARTIAL, isSell = true)
            this.orderHash = createHash()
            this.tradingPair = zrxTradingPair
            this.percentageFilled = 45
            this.status = OrderSummaryFilter.FILTER_OPEN_PARTIAL
            this.amount = BigInteger("25783") * (BigInteger.TEN.pow(tradingPair.primaryToken.decimalPlaces))
            this.priceInSecondaryTicker = BigDecimal("0.0000132123") // The price dropped :-)
            this.isSell = true // Get rid of this ZRX token; we only need LRC
        }
        private val order4 = AppLooprOrder().apply {
            this.orderHash = createHash()
            this.tradingPair = appcTradingPair
            this.percentageFilled = 100
            this.status = OrderSummaryFilter.FILTER_FILLED
            this.amount = BigInteger("7013") * (BigInteger.TEN.pow(tradingPair.primaryToken.decimalPlaces))
            this.priceInSecondaryTicker = BigDecimal("0.0259458")
            this.isSell = true
        }

    }

    private val fill1 = LooprOrderFill(createHash(), order1.orderHash, null, BigInteger("143000000000000000000"), Date(Date().time - 100000L))
    private val fill2 = LooprOrderFill(createHash(), order1.orderHash, null, BigInteger("25500000000000000000"), Date(Date().time - 70000L))
    private val fill3 = LooprOrderFill(createHash(), order1.orderHash, null, BigInteger("1043500000000000000000"), Date(Date().time - 30000L))

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

            val list = when (orderFilter.pageNumber) {
                1 -> RealmList(order1, order2)
                2 -> RealmList(order3, order4)
                else -> throw IllegalStateException("Max page number is 2, found: ${orderFilter.pageNumber}")
            }

            val data = list.apply {
                forEach { it.address = orderFilter.address!! }
            }
            val pagingItems = RealmList<LooprPagingItem>(LooprPagingItem(criteria, orderFilter.pageNumber, 4))
            return@async LooprOrderContainer(criteria = criteria, pagingItems = pagingItems, orderList = data)
        } else {
            throw IOException("No connection")
        }
    }

    override fun getOrderByHash(orderHash: String): Deferred<AppLooprOrder> = async(NET) {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            return@async RealmList(order1, order2, order3, order4).find { it.orderHash == orderHash }!!
        } else {
            throw IOException("No connection")
        }
    }

    override fun getOrderFillsByOrderHash(filter: OrderFillFilter): Deferred<LooprOrderFillContainer> = async(NET) {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            val list = when (filter.pageNumber) {
                1 -> listOf(fill1, fill2)
                2 -> listOf(fill3)
                else -> throw IllegalArgumentException("Page 3+ should never be reached. Page: ${filter.pageNumber}")
            }.also {
                val order = LooprOrderRepository().getOrderByHashNow(filter.orderHash, NET)
                val primaryToken = order?.tradingPair?.primaryToken
                it.forEach {
                    if (primaryToken != null) {
                        it.token = primaryToken
                    }
                    it.orderHash = filter.orderHash
                }
            }

            val data = RealmList<LooprOrderFill>().apply { addAll(list) }

            val criteria = LooprOrderFillContainer.createCriteria(filter)
            val pagingItems = RealmList<LooprPagingItem>(LooprPagingItem(criteria, filter.pageNumber, 3))
            return@async LooprOrderFillContainer(criteria = criteria, pagingItems = pagingItems, orderFillList = data)
        } else {
            throw IOException("No connection")
        }
    }
}