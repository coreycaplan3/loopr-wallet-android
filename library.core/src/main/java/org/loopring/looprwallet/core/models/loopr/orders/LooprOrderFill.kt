package org.loopring.looprwallet.core.models.loopr.orders

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Corey on 4/24/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
open class LooprOrderFill(
        @PrimaryKey var transactionHash: String = "",
        @Index var orderHash: String = "",
        var fillAmount: Double = 0.00,
        var tradeDate: Date = Date()
) : RealmObject()