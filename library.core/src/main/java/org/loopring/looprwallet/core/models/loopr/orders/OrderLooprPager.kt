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
class OrderLooprPager(private val orderFilter: OrderFilter) : LooprAdapterPager<LooprOrder> {

    var orderContainer: LooprOrderContainer? = null

    override val currentPage: Int
        get() = orderFilter.pageNumber

    override val itemsPerPage: Int
        get() = OrderFilter.ITEMS_PER_PAGE

    override val totalNumberOfItems: Int
        get() {
            val orderContainer = orderContainer ?: return -1
            return orderContainer.pagingItems
                    .find { it.criteria == orderContainer.criteria }
                    ?.totalNumberOfItems ?: return -1
        }

    override var data: OrderedRealmCollection<LooprOrder>? = null
        get() = orderContainer?.data

}