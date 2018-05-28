package org.loopring.looprwallet.core.models.loopr.paging

import io.realm.OrderedRealmCollection
import io.realm.RealmModel

/**
 * Created by corey on 5/12/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
interface LooprPagingContainer<T : RealmModel> : RealmModel {

    var criteria: String

    var pageSize: Int

    var pagingItem: LooprPagingItem?

    val requirePagingItem
        get() = pagingItem!!

    val data: OrderedRealmCollection<T>

}