package org.loopring.looprwallet.core.models.loopr.tokens

import io.realm.RealmObject
import io.realm.annotations.Index
import org.loopring.looprwallet.core.models.realm.TrackedRealmObject
import java.math.BigInteger
import java.util.*

/**
 * Created by Corey Caplan on 3/31/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 * @property address The owner of the token
 * @property balance The amount of this token that the user has, padded out with zeroes
 */
open class TokenBalanceInfo : RealmObject(), TrackedRealmObject {

    @Index var address: String = ""

    override var lastUpdated: Date = Date()

    var balance: BigInteger
        get() = BigInteger(mBalance)
        set(value) {
            mBalance = when (value) {
                BigInteger.ZERO -> "0"
                else -> value.toString(10)
            }
        }

    var mBalance: String = BigInteger.ZERO.toString(10)
        private set
}