package org.loopring.looprwallet.core.networking.loopr

import io.realm.RealmList
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrder
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrderContainer
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrderFill
import org.loopring.looprwallet.core.models.loopr.orders.OrderFilter
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

    override fun getOrdersByAddress(orderFilter: OrderFilter): Deferred<LooprOrderContainer> {
        TODO("Have Adam change get orders to allow null parameters")
//        return service.getOrders(
//                orderFilter.address,
//                null,
//                orderFilter.statusFilter,
//                orderFilter.market,
//                null,
//                orderFilter.pageNumber,
//                OrderFilter.ITEMS_PER_PAGE
//        )
    }

    override fun getOrderByHash(orderHash: String): Deferred<LooprOrder> {
        TODO("Have Adam change get orders to allow null parameters")
//        val order = runBlocking<LooprOrder> {
//            val ordersContainer = service.getOrders(
//                    null,
//                    orderHash,
//                    null,
//                    null,
//                    null,
//                    1,
//                    OrderFilter.ITEMS_PER_PAGE
//            ).await()
//            val orders = ordersContainer.orders
//            if (orders != null && orders.size >= 1) {
//                return@runBlocking orders.first()!!
//            } else {
//                throw IllegalStateException("Orders should have at least one item in it!")
//            }
//        }
//
//        return CompletableDeferred<LooprOrder>(order)
    }

    override fun getOrderFillsByOrderHash(orderHash: String): Deferred<RealmList<LooprOrderFill>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}