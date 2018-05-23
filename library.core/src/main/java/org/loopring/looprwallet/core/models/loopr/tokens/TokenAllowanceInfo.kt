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
 * @param address The owner of the token.
 * @param allowance The amount that this user has approved for the Loopring Smart Contract to trade.
 */
open class TokenAllowanceInfo(

        @Index var address: String = "",

        allowance: BigInteger = BigInteger.ZERO,

        override var lastUpdated: Date = Date()
) : RealmObject(), TrackedRealmObject {

    var allowance: BigInteger
        get() = BigInteger(_allowance)
        set(value) {
            _allowance = when (value) {
                BigInteger.ZERO -> "0"
                else -> value.toString(10)
            }
        }

    private var _allowance: String

    init {
        // Needed to set backing fields initially. They will be overwritten by the call below to balance
        this._allowance = ""

        // This will set mBalance and override the initial empty-string value
        this.allowance = allowance
    }

}