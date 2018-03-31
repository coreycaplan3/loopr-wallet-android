package com.caplaninnovations.looprwallet.models.crypto

import com.caplaninnovations.looprwallet.extensions.equalsZero
import com.caplaninnovations.looprwallet.models.TrackedRealmObject
import io.realm.RealmObject
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

        var address: String = "",

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