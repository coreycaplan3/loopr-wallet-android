package org.loopring.looprwallet.core.repositories.loopr

import android.arch.lifecycle.LiveData
import io.realm.Case
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.coroutines.experimental.android.HandlerContext
import kotlinx.coroutines.experimental.android.UI
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.extensions.sort
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrder
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrderContainer
import org.loopring.looprwallet.core.models.loopr.orders.OrderFilter
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

    fun getOrderByHash(orderHash: String, context: HandlerContext = UI): LiveData<LooprOrder> {
        return getRealmFromContext(context)
                .where<LooprOrder>()
                .equalTo(LooprOrder::orderHash, orderHash)
                .findFirstAsync()
                .asLiveData()
    }

    fun getOrders(orderFilter: OrderFilter, context: HandlerContext = UI): LiveData<LooprOrderContainer> {
        return getRealmFromContext(context)
                .where<LooprOrderContainer>()
                .apply {
                    // Market
                    val marketPropertyList = listOf(LooprOrderContainer::data, LooprOrder::tradingPair)
                    val marketProperty = TradingPair::market

                    if (orderFilter.market != null) {
                        equalTo(marketPropertyList, marketProperty, "${orderFilter.market}", Case.INSENSITIVE)
                    }

                    if (orderFilter.address != null) {
                        equalTo(LooprOrder::address, orderFilter.address)
                    }

                    equalTo(LooprOrder::status, orderFilter.status)
                }
                .sort(LooprOrder::orderDate, Sort.DESCENDING)
                .findFirstAsync()
                .asLiveData()
    }

}