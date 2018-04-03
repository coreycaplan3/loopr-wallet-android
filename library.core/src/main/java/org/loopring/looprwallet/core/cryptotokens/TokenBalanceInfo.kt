package org.loopring.looprwallet.core.cryptotokens

import io.realm.RealmObject
import io.realm.annotations.Index
import org.loopring.looprwallet.core.extensions.equalsZero
import org.loopring.looprwallet.core.models.TrackedRealmObject
import java.math.BigDecimal
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

        balance: BigDecimal = BigDecimal.ZERO,

        override var lastUpdated: Date = Date()
) : RealmObject(), TrackedRealmObject {

    var balance: BigDecimal?
        get() = mBalance?.let { BigDecimal(it) }
        set(value) {
            mBalance = when {
                value?.equalsZero() == true -> "0"
                else -> value?.toPlainString()
            }
        }

    var mBalance: String?

    init {
        // Needed to set backing fields initially. They will be overwritten by the two calls below
        this.mBalance = null

        // This will set mBalance and override the initial null value
        this.balance = balance
    }

}