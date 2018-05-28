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
class OrderSummaryPager : LooprAdapterPager<AppLooprOrder> {

    var orderContainer: LooprOrderContainer? = null

    override val currentPage: Int
        get() {
            val orderContainer = orderContainer ?: return -1
            return orderContainer.requirePagingItem.pageIndex
        }

    override val itemsPerPage: Int
        get() = OrderSummaryFilter.ITEMS_PER_PAGE

    override val totalNumberOfItems: Int
        get() {
            val orderContainer = orderContainer ?: return -1
            return orderContainer.requirePagingItem.totalNumberOfItems
        }

    override var data: OrderedRealmCollection<AppLooprOrder>? = null
        get() = orderContainer?.data

}