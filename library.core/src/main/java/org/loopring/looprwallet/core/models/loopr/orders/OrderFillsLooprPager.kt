package org.loopring.looprwallet.core.models.loopr.orders

import io.realm.OrderedRealmCollection
import org.loopring.looprwallet.core.models.loopr.paging.LooprAdapterPager

/**
 * Created by corey on 5/14/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class OrderFillsLooprPager : LooprAdapterPager<LooprOrderFill> {

    var orderFillContainer: LooprOrderFillContainer? = null

    override val currentPage: Int
        get() {
            val container = orderFillContainer ?: return -1
            return container.requirePagingItem.pageIndex
        }

    override val itemsPerPage: Int
        get() = OrderFillFilter.ITEMS_PER_PAGE

    override val totalNumberOfItems: Int
        get() {
            val container = orderFillContainer ?: return -1
            return container.requirePagingItem.totalNumberOfItems
        }

    override var data: OrderedRealmCollection<LooprOrderFill>? = null
        get() = orderFillContainer?.data

}