package org.loopring.looprwallet.core.repositories.loopr

import android.arch.lifecycle.LiveData
import io.realm.*
import io.realm.kotlin.where
import kotlinx.coroutines.experimental.android.HandlerContext
import kotlinx.coroutines.experimental.android.UI
import org.loopring.looprwallet.core.extensions.*
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.loopr.orders.AppLooprOrder
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrderContainer
import org.loopring.looprwallet.core.models.loopr.orders.OrderSummaryFilter
import org.loopring.looprwallet.core.models.loopr.paging.LooprPagingItem
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.repositories.BaseRealmRepository

/**
 * Created by Corey Caplan on 4/7/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class LooprOrderRepository : BaseRealmRepository() {

    fun cancelOrder(orderHash: String, context: HandlerContext = IO) = runTransaction(context) {
        val filter = OrderSummaryFilter(status = OrderSummaryFilter.FILTER_OPEN_ALL, orderHash = orderHash)
        cancelOrdersAndUpdateDatabase(filter, it, context)
    }

    fun cancelOrdersByTradingPair(address: String, market: String, context: HandlerContext = IO) = runTransaction(context) {
        val filter = OrderSummaryFilter(address = address, market = market, status = OrderSummaryFilter.FILTER_OPEN_ALL)
        cancelOrdersAndUpdateDatabase(filter, it, context)
    }

    fun cancelAllOpenOrders(address: String, context: HandlerContext = IO) = runTransaction(context) {
        val filter = OrderSummaryFilter(address = address, status = OrderSummaryFilter.FILTER_OPEN_ALL)
        cancelOrdersAndUpdateDatabase(filter, it, context)
    }

    fun getOrderContainerNow(filter: OrderSummaryFilter, context: HandlerContext = UI): LooprOrderContainer? {
        val item = getRealmFromContext(context)
                .where<LooprOrderContainer>()
                .equalTo(LooprOrderContainer::criteria, LooprOrderContainer.createCriteria(filter), Case.INSENSITIVE)
                .findFirst()

        return item?.let {
            getRealmFromContext(context)
                    .copyFromRealm(it)
                    .also { container ->
                        container.orderList = getOrdersByFilterNow(filter, context)
                    }
        }
    }

    /**
     * @return An **UN-MANAGED** [AppLooprOrder] that is a deep copy from realm
     */
    fun getOrderByHashNow(orderHash: String, context: HandlerContext = UI): AppLooprOrder? {
        val order = getRealmFromContext(context)
                .where<AppLooprOrder>()
                .equalTo(AppLooprOrder::orderHash, orderHash, Case.INSENSITIVE)
                .findFirst()

        return order?.let { getRealmFromContext(context).copyFromRealm(it) }
    }

    fun getOrderByHash(filter: OrderSummaryFilter, context: HandlerContext = UI): LiveData<AppLooprOrder> {
        return getRealmFromContext(context)
                .where<AppLooprOrder>()
                .also { applyFilterToQuery(it, filter) }
                .findFirstAsync()
                .asLiveData()
    }

    fun getOrdersByFilterNow(filter: OrderSummaryFilter, context: HandlerContext = UI): RealmResults<AppLooprOrder> {
        return getRealmFromContext(context)
                .where<AppLooprOrder>()
                .also {
                    applyFilterToQuery(it, filter)
                }
                .findAll()
    }

    /**
     * @return A new list of items that are backed by a realm async query for filtering the existing
     * data-set
     */
    fun filterOrdersByQuery(searchQuery: String, data: OrderedRealmCollection<AppLooprOrder>): OrderedRealmCollection<AppLooprOrder> {
        return data.where()
                .like(listOf(AppLooprOrder::mTradingPair), TradingPair::market, "*$searchQuery*", Case.INSENSITIVE)
                .or()
                .like(listOf(AppLooprOrder::mTradingPair, TradingPair::mPrimaryToken), LooprToken::name, "*$searchQuery*", Case.INSENSITIVE)
                .findAllAsync()
    }

    fun getOrders(filter: OrderSummaryFilter, context: HandlerContext = UI): OrderedRealmCollection<AppLooprOrder> {
        return getRealmFromContext(context)
                .where<AppLooprOrder>()
                .also {
                    applyFilterToQuery(it, filter)
                }
                .findAllAsync()
    }

    fun getOrderContainer(filter: OrderSummaryFilter, context: HandlerContext = UI): LiveData<OrderedRealmCollection<LooprOrderContainer>> {
        return getRealmFromContext(context)
                .where<LooprOrderContainer>()
                .equalTo(LooprOrderContainer::criteria, LooprOrderContainer.createCriteria(filter), Case.INSENSITIVE)
                .findAllAsync()
                .asLiveData()
    }

    // MARK - Private Methods

    private fun applyFilterToQuery(query: RealmQuery<AppLooprOrder>, filter: OrderSummaryFilter) {
        // Markets
        if (filter.market != null) {
            query.equalTo(listOf(AppLooprOrder::tradingPair), TradingPair::market, filter.market, Case.INSENSITIVE)
        }

        // Address
        if (filter.address != null) {
            query.equalTo(AppLooprOrder::address, filter.address, Case.INSENSITIVE)
        }

        // Status
        when {
            filter.status == OrderSummaryFilter.FILTER_OPEN_ALL -> {
                val statuses = arrayOf(OrderSummaryFilter.FILTER_OPEN_PARTIAL, OrderSummaryFilter.FILTER_OPEN_NEW)
                query.`in`(AppLooprOrder::status.name, statuses, Case.INSENSITIVE)
            }
            filter.status != null -> query.equalTo(AppLooprOrder::status, filter.status, Case.INSENSITIVE)
        }

        // Order Hash
        if (filter.orderHash != null) {
            query.equalTo(AppLooprOrder::orderHash, filter.orderHash, Case.INSENSITIVE)
        }

        // Sort
        query.sort(AppLooprOrder::orderDate, Sort.DESCENDING)
    }

    // MARK - Private Methods

    private fun cancelOrdersAndUpdateDatabase(filter: OrderSummaryFilter, realm: Realm, context: HandlerContext) {
        val orders = getOrdersByFilterNow(filter, context).createSnapshot().also {
            it.forEach {
                it.status = OrderSummaryFilter.FILTER_CANCELLED
            }
        }

        val order = orders.first()
        val addressFilter = OrderSummaryFilter(address = order?.address, status = OrderSummaryFilter.FILTER_OPEN_ALL)
        val addressContainer = getOrderContainerNow(addressFilter, context)?.also {
            it.requirePagingItem.totalNumberOfItems -= orders.size
        }

        val marketsAddressFilter = OrderSummaryFilter(address = order?.address, market = order?.tradingPair?.market, status = OrderSummaryFilter.FILTER_OPEN_ALL)
        val marketAddressContainer = getOrderContainerNow(marketsAddressFilter, context)?.also {
            it.requirePagingItem.totalNumberOfItems -= orders.size
        }

        val cancelledFilter = OrderSummaryFilter(filter.address, filter.market, OrderSummaryFilter.FILTER_CANCELLED, filter.orderHash)
        val cancelledContainer = getOrderContainerNow(cancelledFilter, context).let {
            when (it) {
                null -> {
                    val criteria = LooprOrderContainer.createCriteria(cancelledFilter)
                    val pagingItem = LooprPagingItem(criteria, 1, orders.size)
                    realm.upsert(pagingItem)
                    LooprOrderContainer(criteria = criteria, pagingItem = pagingItem)
                }
                else -> {
                    it.requirePagingItem.totalNumberOfItems += orders.size
                    it
                }
            }
        }

        realm.upsert(orders)
        addressContainer?.let { realm.upsert(it) }
        marketAddressContainer?.let { realm.upsert(it) }
        realm.upsert(cancelledContainer)
    }

}