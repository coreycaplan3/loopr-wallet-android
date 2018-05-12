package org.loopring.looprwallet.core.models.loopr.paging

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by corey on 5/12/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
open class LooprPagingItem(
        @PrimaryKey var criteria: String = "",
        var pageIndex: Int = 1,
        var totalNumberOfItems: Int = 0
) : RealmObject()