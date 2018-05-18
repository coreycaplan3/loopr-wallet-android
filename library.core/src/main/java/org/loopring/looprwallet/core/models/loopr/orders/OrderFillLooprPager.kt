package org.loopring.looprwallet.core.models.loopr.orders

import io.realm.OrderedRealmCollection
import org.loopring.looprwallet.core.models.loopr.paging.LooprAdapterPager

/**
 * Created by corey on 5/12/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class OrderFillLooprPager(val orderFillFilter: OrderFillFilter) : LooprAdapterPager<LooprOrderFill> {

    var orderFillContainer: LooprOrderFillContainer? = null

    override val currentPage: Int
        get() {
            val container = orderFillContainer ?: return orderFillFilter.pageNumber
            return container.pagingItems
                    .find { it.criteria == container.criteria }
                    ?.totalNumberOfItems ?: return orderFillFilter.pageNumber
        }

    override val itemsPerPage: Int
        get() = OrderFillFilter.ITEMS_PER_PAGE

    override val totalNumberOfItems: Int
        get() {
            val container = orderFillContainer ?: return -1
            return container.pagingItems
                    .find { it.criteria == container.criteria }
                    ?.totalNumberOfItems ?: return -1
        }

    override var data: OrderedRealmCollection<LooprOrderFill>? = null
        get() = orderFillContainer?.data

}