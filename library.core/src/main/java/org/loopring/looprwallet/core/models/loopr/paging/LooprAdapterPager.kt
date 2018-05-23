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
     * The total number of [data] items there are to load. This value should be **null** if the
     * number of items is returned all in one request (so *loading more* isn't used)
     */
    val totalNumberOfItems: Int?

    /**
     * The data in this collection wrapper, backed by realm.
     */
    var data: OrderedRealmCollection<T>?

    /**
     * Possible Values:
     * - True if more data can be loaded from the network
     * - False if the app already loaded all the data
     * - Null if all of the data can be retrieved in one request and *loading more* isn't necessary
     */
    val containsMoreData: Boolean?
        get() {
            val data = data ?: return null
            val totalNumberOfItems = totalNumberOfItems ?: return null

            return data.size < totalNumberOfItems
        }

}