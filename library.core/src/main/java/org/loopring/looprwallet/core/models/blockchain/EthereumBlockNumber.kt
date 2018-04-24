package org.loopring.looprwallet.core.models.blockchain

import io.realm.RealmObject
import org.loopring.looprwallet.core.models.TrackedRealmObject
import java.math.BigInteger
import java.util.*

/**
 * Created by Corey on 4/23/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class: To show the last block number synchronized and the time at which it was last
 * updated
 */
open class EthereumBlockNumber(
        blockNumber: BigInteger = BigInteger.ZERO
) : RealmObject() {

    /**
     * The block number, represented as a base 10 number.
     */
    var blockNumber: BigInteger
        get() = BigInteger(mBlockNumber)
        set(value) {
            mBlockNumber = blockNumber.toString(10)
        }

    private var mBlockNumber: String

    init {
        mBlockNumber = "" // Temporary until [this.blockNumber] is set below
        this.blockNumber = blockNumber
    }

}