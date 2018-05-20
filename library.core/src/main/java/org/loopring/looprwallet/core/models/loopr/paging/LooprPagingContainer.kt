package org.loopring.looprwallet.core.models.loopr.paging

import io.realm.OrderedRealmCollection
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.annotations.Ignore

/**
 * Created by corey on 5/12/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
interface LooprPagingContainer<T : RealmModel>: RealmModel {

    var criteria: String

    var pageSize: Int

    var pagingItems: RealmList<LooprPagingItem>

    val data: OrderedRealmCollection<T>

}