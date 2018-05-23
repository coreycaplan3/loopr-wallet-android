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
open class LooprOrderFill(
        @PrimaryKey var transactionHash: String = "",
        @Index var orderHash: String = "",
        token: LooprToken? = null,
        fillAmount: BigInteger = BigInteger.ZERO,
        var tradeDate: Date = Date()
) : RealmObject() {

    private var _token: LooprToken? = token

    var token: LooprToken
        get() = _token!!
        set(value) {
            _token = value
        }

    private var _fillAmount: String = fillAmount.toString(10)

    var fillAmount: BigInteger
        get() = BigInteger(_fillAmount)
        set(value) {
            _fillAmount = value.toString(10)
        }

}