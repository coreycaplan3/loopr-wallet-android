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
class OrderSummaryPager(private val orderFilter: OrderSummaryFilter) : LooprAdapterPager<AppLooprOrder> {

    var orderContainer: LooprOrderContainer? = null

    override val currentPage: Int
        get() = TODO("check the home order")

    override val itemsPerPage: Int
        get() = OrderSummaryFilter.ITEMS_PER_PAGE

    override val totalNumberOfItems: Int
        get() {
            val orderContainer = orderContainer ?: return -1
            return orderContainer.pagingItems
                    .find { it.criteria == orderContainer.criteria }
                    ?.totalNumberOfItems ?: return -1
        }

    override var data: OrderedRealmCollection<AppLooprOrder>? = null
        get() = orderContainer?.data

}