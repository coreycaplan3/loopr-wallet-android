package org.loopring.looprwallet.core.repositories.loopr

import android.arch.lifecycle.LiveData
import io.realm.Case
import io.realm.OrderedRealmCollection
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.coroutines.experimental.android.HandlerContext
import kotlinx.coroutines.experimental.android.UI
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.extensions.sort
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.loopr.orders.AppLooprOrder
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrderContainer
import org.loopring.looprwallet.core.models.loopr.orders.OrderSummaryFilter
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

    fun getOrderContainerByKeyNow(criteria: String, context: HandlerContext = UI): LooprOrderContainer? {
        val item = getRealmFromContext(context)
                .where<LooprOrderContainer>()
                .equalTo(LooprOrderContainer::criteria, criteria)
                .findFirst()

        return item?.let { getRealmFromContext(context).copyFromRealm(it) }
    }

    /**
     * @return An **UN-MANAGED** [AppLooprOrder] that is a deep copy from realm
     */
    fun getOrderByHashNow(orderHash: String, context: HandlerContext = UI): AppLooprOrder? {
        val order = getRealmFromContext(context)
                .where<AppLooprOrder>()
                .equalTo(AppLooprOrder::orderHash, orderHash)
                .findFirst()

        return order?.let { getRealmFromContext(context).copyFromRealm(it) }
    }

    fun getOrderByHash(orderHash: String, context: HandlerContext = UI): LiveData<AppLooprOrder> {
        return getRealmFromContext(context)
                .where<AppLooprOrder>()
                .equalTo(AppLooprOrder::orderHash, orderHash)
                .findFirstAsync()
                .asLiveData()
    }

    fun getOrdersByFilterNow(orderFilter: OrderSummaryFilter, context: HandlerContext = UI): RealmResults<AppLooprOrder> {
        return getRealmFromContext(context)
                .where<AppLooprOrder>()
                .apply {
                    // Market
                    val marketProperty = TradingPair::market

                    if (orderFilter.market != null) {
                        equalTo(marketProperty, "${orderFilter.market}", Case.INSENSITIVE)
                    }

                    if (orderFilter.address != null) {
                        equalTo(AppLooprOrder::address, orderFilter.address)
                    }

                    equalTo(AppLooprOrder::status, orderFilter.status)
                }
                .findAll()
    }

    fun getOrders(filter: OrderSummaryFilter, context: HandlerContext = UI): OrderedRealmCollection<AppLooprOrder> {
        return getRealmFromContext(context)
                .where<AppLooprOrder>()
                .apply {

                    if (filter.market != null) {
                        equalTo(listOf(AppLooprOrder::tradingPair), TradingPair::market, filter.market, Case.INSENSITIVE)
                    }

                    if (filter.address != null) {
                        equalTo(AppLooprOrder::address, filter.address)
                    }

                    when (filter.status) {
                        OrderSummaryFilter.FILTER_OPEN_ALL -> {
                            val statuses = arrayOf(OrderSummaryFilter.FILTER_OPEN_PARTIAL, OrderSummaryFilter.FILTER_OPEN_NEW)
                            `in`(AppLooprOrder::status.name, statuses)
                        }
                        else -> equalTo(AppLooprOrder::status, filter.status)
                    }

                    sort(AppLooprOrder::orderDate, Sort.DESCENDING)
                }
                .findAllAsync()
    }

    fun getOrderContainer(filter: OrderSummaryFilter, context: HandlerContext = UI): LooprOrderContainer {
        return getRealmFromContext(context)
                .where<LooprOrderContainer>()
                .equalTo(LooprOrderContainer::criteria, LooprOrderContainer.createCriteria(filter))
                .findFirstAsync()
    }

}