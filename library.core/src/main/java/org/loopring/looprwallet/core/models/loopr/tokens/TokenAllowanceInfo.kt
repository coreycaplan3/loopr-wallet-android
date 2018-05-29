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
 * @property address The owner of the token.
 * @property allowance The amount that this user has approved for the Loopring Smart Contract
 * Delegate to trade.
 */
open class TokenAllowanceInfo : RealmObject(), TrackedRealmObject {

    @Index var address: String = ""

    override var lastUpdated: Date = Date()

    var allowance: BigInteger
        get() = BigInteger(mAllowance)
        set(value) {
            mAllowance = when (value) {
                BigInteger.ZERO -> "0"
                else -> value.toString(10)
            }
        }

    private var mAllowance: String = BigInteger.ZERO.toString(10)

}