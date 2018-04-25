package org.loopring.looprwallet.core.models.transfers

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import org.loopring.looprwallet.core.extensions.ifNotNull
import org.loopring.looprwallet.core.models.cryptotokens.LooprToken
import java.math.BigInteger
import java.util.*

/**
 * Created by Corey Caplan on 2/28/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To represent a token send/receive event between two users.
 *
 * @property transactionHash  The unique hash of the transaction
 * @property timestamp  The time UNIX timestamp (in millis) at which the transfer occurred.
 * @property numberOfTokens
 */
open class LooprTransfer(
        @PrimaryKey
        var transactionHash: String = "",

        @Index
        var timestamp: Date = Date(),

        var isSend: Boolean = false,

        blockNumber: BigInteger? = null,

        var contactAddress: String = "",

        numberOfTokens: BigInteger = BigInteger.ZERO,

        usdValue: BigInteger = BigInteger.ZERO,

        transactionFee: BigInteger = BigInteger.ZERO,

        transactionFeeUsdValue: BigInteger = BigInteger.ZERO,

        token: LooprToken = LooprToken.ETH
) : RealmObject() {

    private var mBlockNumber: String? = null
    private var mNumberOfTokens: String = ""
    private var mUsdValue: String = ""
    private var mTransactionFee: String = ""
    private var mTransactionFeeUsdValue: String = ""

    var mToken: LooprToken? = token

    // TODO better way of handling this
    var token: LooprToken
        get() = mToken!!
        set(value) {
            mToken = value
        }

    /**
     * The block number at which the transfer was mined
     */
    var blockNumber: BigInteger?
        get() = mBlockNumber?.let { BigInteger(it) }
        set(value) {
            mBlockNumber = value?.toString(10)
        }

    /**
     * The number of tokens that were sent in this transaction. To get the decimal representation,
     * divide this number by (10 ^ [LooprToken.decimalPlaces]). The radix for this number is
     * **ALWAYS** 10.
     */
    var numberOfTokens: BigInteger
        get() = BigInteger(mNumberOfTokens)
        set(value) {
            mNumberOfTokens = value.toString(10)
        }

    /**
     * The value of the currency at the time of the transaction. To get the decimal representation
     * of this number, divide the transaction fee by (100). The radix for this number is **ALWAYS**
     * 10.
     */
    var usdValue: BigInteger
        get() = BigInteger(mUsdValue)
        set(value) {
            mUsdValue = value.toString(10)
        }

    /**
     * The amount paid by the sender (in gas) to execute this transfer. To get the decimal
     * representation of this number, divide the transaction fee by (10^18). The radix for this
     * number is **ALWAYS** 10.
     */
    var transactionFee: BigInteger
        get() = BigInteger(mTransactionFee)
        set(value) {
            mTransactionFee = value.toString(10)
        }

    /**
     * The amount paid by the sender (in USD) to execute this transfer. To get the decimal
     * representation of this number, divide the transaction fee by (100). The radix for this
     * number is **ALWAYS** 10.
     */
    var transactionFeeUsdValue: BigInteger
        get() = BigInteger(mTransactionFeeUsdValue)
        set(value) {
            mTransactionFeeUsdValue = value.toString(10)
        }

    init {
        this.mToken?.ifNotNull { this.token = it }
        this.blockNumber = blockNumber
        this.numberOfTokens = numberOfTokens
        this.usdValue = usdValue
        this.transactionFee = transactionFee
        this.transactionFeeUsdValue = transactionFeeUsdValue
    }

}