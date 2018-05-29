package org.loopring.looprwallet.core.models.loopr.tokens

import io.realm.RealmObject
import io.realm.annotations.Index
import org.loopring.looprwallet.core.models.realm.TrackedRealmObject
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import java.math.BigInteger
import java.util.*

/**
 * Created by corey on 5/29/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 * @property currency The currency that the price is described using
 * @see CurrencySettings
 */
open class TokenPriceInfo(@Index var currency: String = CurrencySettings.USD) : RealmObject(), TrackedRealmObject {

    override var lastUpdated: Date = Date()

    private var mPrice: String = BigInteger.ZERO.toString(10)

    var price: BigInteger
        get() = BigInteger(mPrice)
        set(value) {
            mPrice = when (value) {
                BigInteger.ZERO -> "0"
                else -> value.toString(10)
            }
        }

}