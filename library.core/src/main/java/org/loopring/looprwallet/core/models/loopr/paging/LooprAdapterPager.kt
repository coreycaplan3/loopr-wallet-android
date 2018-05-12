package org.loopring.looprwallet.core.models.loopr.paging

import io.realm.OrderedRealmCollection
import io.realm.RealmModel

/**
 * Created by corey on 5/12/18
 *
 * Project: loopr-android
 *
 * Purpose of Class: A wrapper around a collection of [T] that adds essential paging information
 * so the user knows when there's more data available in a scrolling list of content.
 */
interface LooprAdapterPager<T : RealmModel> {

    /**
     * The current page of data
     */
    val currentPage: Int

    /**
     * The number of items per page of data
     */
    val itemsPerPage: Int

    /**
     * The total number of [data] items there are to load
     */
    val totalNumberOfItems: Int

    /**
     * The data in this collection wrapper
     */
    var data: OrderedRealmCollection<T>?

    /**
     * True if more data can be loaded from the network or false if the app already loaded all the
     * data.
     */
    val containsMoreData: Boolean
        get() {
            val data = data ?: return false

            return data.size < totalNumberOfItems
        }

}