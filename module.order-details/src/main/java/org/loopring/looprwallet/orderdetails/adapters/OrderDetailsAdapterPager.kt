package org.loopring.looprwallet.orderdetails.adapters

import io.realm.OrderedRealmCollection
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrderContainer
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrderFill
import org.loopring.looprwallet.core.models.loopr.orders.OrderFillFilter
import org.loopring.looprwallet.core.models.loopr.paging.LooprAdapterPager

/**
 * Created by corey on 5/14/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class OrderDetailsAdapterPager(val orderFillFilter: OrderFillFilter) : LooprAdapterPager<LooprOrderFill> {

    var orderFillContainer: LooprOrderContainer? = null

    override val currentPage: Int
        get() = orderFillFilter.pageNumber

    override val itemsPerPage: Int
        get() = OrderFillFilter.ITEMS_PER_PAGE

    override val totalNumberOfItems: Int
        get() = orderFillContainer?.let { container ->
            container.pagingItems.find { it.criteria == container.criteria }?.totalNumberOfItems
        } ?: -1

    override var data: OrderedRealmCollection<LooprOrderFill>?
        get() = orderFillContainer?.data as? OrderedRealmCollection<LooprOrderFill>
        set(value) {}
}