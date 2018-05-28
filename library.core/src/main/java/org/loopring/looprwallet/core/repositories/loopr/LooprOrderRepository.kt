package org.loopring.looprwallet.core.repositories.loopr

import android.arch.lifecycle.LiveData
import io.realm.*
import io.realm.kotlin.where
import kotlinx.coroutines.experimental.android.HandlerContext
import kotlinx.coroutines.experimental.android.UI
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.extensions.like
import org.loopring.looprwallet.core.extensions.sort
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.loopr.orders.AppLooprOrder
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrderContainer
import org.loopring.looprwallet.core.models.loopr.orders.OrderSummaryFilter
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

    fun getOrderByHash(orderHash: String, context: HandlerContext = UI): LiveData<AppLooprOrder> {
        return getRealmFromContext(context)
                .where<AppLooprOrder>()
                .equalTo(AppLooprOrder::orderHash, orderHash, Case.INSENSITIVE)
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
        when (filter.status) {
            OrderSummaryFilter.FILTER_OPEN_ALL -> {
                val statuses = arrayOf(OrderSummaryFilter.FILTER_OPEN_PARTIAL, OrderSummaryFilter.FILTER_OPEN_NEW)
                query.`in`(AppLooprOrder::status.name, statuses, Case.INSENSITIVE)
            }
            else -> query.equalTo(AppLooprOrder::status, filter.status, Case.INSENSITIVE)
        }

        // Sort
        query.sort(AppLooprOrder::orderDate, Sort.DESCENDING)
    }

}