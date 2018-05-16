package org.loopring.looprwallet.core.networking.loopr

import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.loopr.orders.*
import org.loopring.looprwalletnetwork.services.LoopringService

/**
 * Created by corey on 5/12/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class LooprOrderServiceProdImpl : LooprOrderService {

    private val service by lazy {
        LoopringService()
    }

    override fun submitOrder(looprOrder: AppLooprOrder): Deferred<Unit> {
        TODO("not implemented")
    }

    override fun getOrdersByAddress(orderFilter: OrderSummaryFilter): Deferred<LooprOrderContainer> {
        TODO("Have Adam change get orders to allow null parameters")
//        return service.getOrders(
//                orderFilter.address,
//                null,
//                orderFilter.statusFilter,
//                orderFilter.market,
//                null,
//                orderFilter.pageNumber,
//                OrderSummaryFilter.ITEMS_PER_PAGE
//        )
    }

    override fun getOrderByHash(orderHash: String): Deferred<AppLooprOrder> {
        TODO("Have Adam change get orders to allow null parameters")
//        val order = runBlocking<AppLooprOrder> {
//            val ordersContainer = service.getOrders(
//                    null,
//                    orderHash,
//                    null,
//                    null,
//                    null,
//                    1,
//                    OrderSummaryFilter.ITEMS_PER_PAGE
//            ).await()
//            val orders = ordersContainer.orders
//            if (orders != null && orders.size >= 1) {
//                return@runBlocking orders.first()!!
//            } else {
//                throw IllegalStateException("Orders should have at least one item in it!")
//            }
//        }
//
//        return CompletableDeferred<AppLooprOrder>(order)
    }

    override fun getOrderFillsByOrderHash(orderFillFilter: OrderFillFilter): Deferred<LooprOrderFillContainer> {
        TODO("not implemented")
    }
}