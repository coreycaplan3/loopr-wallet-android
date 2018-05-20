package org.loopring.looprwallet.orderdetails.repositories

import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import io.realm.RealmResults
import io.realm.kotlin.where
import kotlinx.coroutines.experimental.android.HandlerContext
import kotlinx.coroutines.experimental.android.UI
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrderFill
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrderFillContainer
import org.loopring.looprwallet.core.models.loopr.orders.OrderFillFilter
import org.loopring.looprwallet.core.models.loopr.paging.LooprPagingItem
import org.loopring.looprwallet.core.repositories.BaseRealmRepository

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class LooprOrderFillsRepository : BaseRealmRepository() {

    fun getOrderFillContainerByKeyNow(criteria: String, context: HandlerContext = UI): LooprOrderFillContainer? {
        val container = getRealmFromContext(context)
                .where<LooprOrderFillContainer>()
                .equalTo(listOf(LooprOrderFillContainer::pagingItems), LooprPagingItem::criteria, criteria)
                .findFirst()

        return container?.let { getRealmFromContext(context).copyFromRealm(it) }
    }

    fun getOrderFillsNow(orderFillFilter: OrderFillFilter, context: HandlerContext = IO): RealmResults<LooprOrderFill> {
        return getRealmFromContext(context)
                .where<LooprOrderFill>()
                .equalTo(LooprOrderFill::orderHash, orderFillFilter.orderHash)
                .findAll()
    }

    fun getOrderFills(orderFillFilter: OrderFillFilter, context: HandlerContext = UI): OrderedRealmCollection<LooprOrderFill> {
        return getRealmFromContext(context)
                .where<LooprOrderFill>()
                .equalTo(LooprOrderFill::orderHash, orderFillFilter.orderHash)
                .findAllAsync()
    }

    fun getOrderFillContainer(filter: OrderFillFilter, context: HandlerContext = UI): LooprOrderFillContainer {
        return getRealmFromContext(context)
                .where<LooprOrderFillContainer>()
                .equalTo(LooprOrderFillContainer::criteria, LooprOrderFillContainer.createCriteria(filter))
                .findFirstAsync()
    }

}