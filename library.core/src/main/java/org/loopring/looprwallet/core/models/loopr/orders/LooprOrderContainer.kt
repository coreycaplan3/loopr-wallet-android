package org.loopring.looprwallet.core.models.loopr.orders

import io.realm.OrderedRealmCollection
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import org.loopring.looprwallet.core.models.loopr.paging.LooprPagingContainer
import org.loopring.looprwallet.core.models.loopr.paging.LooprPagingItem

/**
 * Created by corey on 5/12/18
 *
 * Project: loopr-android
 *
 * Purpose of Class: A container around [AppLooprOrder] that tracks the current page number of the
 * network's result set.
 *
 * @property criteria A key created by calling [createCriteria] using an [OrderSummaryFilter].
 * @property pagingItems A mapping of the criteria used to produce this container
 * to the page index and total number of items for that criteria. Useful for distinguishing between
 * getting total number of orders by address and total by trading pair, etc. Created by calling
 * [createCriteria]
 */
open class LooprOrderContainer(
        @PrimaryKey override var criteria: String = "",
        override var pageSize: Int = 50,
        override var pagingItems: RealmList<LooprPagingItem> = RealmList(),
        @Ignore var orderList: OrderedRealmCollection<AppLooprOrder> = RealmList()
) : RealmObject(), LooprPagingContainer<AppLooprOrder> {

    override val data: OrderedRealmCollection<AppLooprOrder>
        get() = orderList

    companion object {

        /**
         * Creates the criteria key to be used with [pagingItems].
         */
        fun createCriteria(filter: OrderSummaryFilter): String = filter.let {
            val address = it.address?.toUpperCase()
            val market = it.market?.toUpperCase()
            val status = it.status.toUpperCase()
            "$address-$market-$status"
        }
    }

}