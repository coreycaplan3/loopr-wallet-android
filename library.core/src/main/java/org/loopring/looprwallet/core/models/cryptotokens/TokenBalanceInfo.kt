package org.loopring.looprwallet.core.models.cryptotokens

import io.realm.RealmObject
import io.realm.annotations.Index
import org.loopring.looprwallet.core.extensions.equalsZero
import org.loopring.looprwallet.core.models.TrackedRealmObject
import java.math.BigInteger
import java.util.*

/**
 * Created by Corey Caplan on 3/31/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 * @param address The owner of the token.
 * @param balance The amount of this token that the user has.
 */
open class TokenBalanceInfo(

        @Index var address: String = "",

        balance: BigInteger = BigInteger.ZERO,

        override var lastUpdated: Date = Date()
) : RealmObject(), TrackedRealmObject {

    var balance: BigInteger
        get() = BigInteger(mBalance)
        set(value) {
            mBalance = when (value) {
                BigInteger.ZERO -> "0"
                else -> value.toString(10)
            }
        }

    var mBalance: String

    init {
        // Needed to set backing fields initially. They will be overwritten by the call below to balance
        this.mBalance = ""

        // This will set mBalance and override the initial empty-string value
        this.balance = balance
    }

}