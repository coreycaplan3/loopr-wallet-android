package org.loopring.looprwallet.core.models.loopr.paging

import io.realm.OrderedRealmCollection
import io.realm.RealmModel

/**
 * Created by corey on 5/12/18
 *
 * Project: loopr-android
 *
 * Purpose of Class: A default implementation of [LooprAdapterPager] that only uses [data] and
 * returns -1 or false for all other field variables.
 *
 */
class DefaultLooprPagerAdapter<T : RealmModel> : LooprAdapterPager<T> {

    override val currentPage: Int
        get() = -1

    override val itemsPerPage: Int
        get() = -1

    override val totalNumberOfItems: Int? = null

    override var data: OrderedRealmCollection<T>? = null

}