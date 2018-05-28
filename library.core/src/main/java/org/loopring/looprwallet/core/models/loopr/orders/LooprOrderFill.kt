package org.loopring.looprwallet.core.models.loopr.orders

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import java.math.BigInteger
import java.util.*

/**
 * Created by Corey on 4/24/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
open class LooprOrderFill : RealmObject() {

    @PrimaryKey var transactionHash: String = ""

    @Index
    var orderHash: String = ""

    var tradeDate: Date = Date()

    private var mToken: LooprToken? = null

    var token: LooprToken
        get() = mToken!!
        set(value) {
            mToken = value
        }

    private var mFillAmount: String = BigInteger.ZERO.toString(10)

    var fillAmount: BigInteger
        get() = BigInteger(mFillAmount)
        set(value) {
            mFillAmount = value.toString(10)
        }

}