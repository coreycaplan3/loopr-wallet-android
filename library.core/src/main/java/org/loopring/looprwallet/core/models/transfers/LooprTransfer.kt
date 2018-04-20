package org.loopring.looprwallet.core.models.transfers

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import org.loopring.looprwallet.core.models.blockchain.LooprTransaction
import org.loopring.looprwallet.core.models.cryptotokens.LooprToken
import java.math.BigInteger

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
class LooprTransfer(
        @PrimaryKey
        var transactionHash: String = "",

        @Index
        var timestamp: Long = -1L,

        numberOfTokens: BigInteger,

        usdValue: BigInteger,

        transactionFee: BigInteger,

        transactionFeeUsdValue: BigInteger,

        var token: LooprToken = LooprToken.ETH,

        /**
         * The current status of the transaction
         */
        @LooprTransaction.TransactionStatus
        var status: Int

) : RealmObject() {

    private var mNumberOfTokens: String = ""
    private var mUsdValue: String = ""
    private var mTransactionFee: String = ""
    private var mTransactionFeeUsdValue: String = ""

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
        this.numberOfTokens = numberOfTokens
        this.usdValue = usdValue
        this.transactionFee = transactionFee
        this.transactionFeeUsdValue = transactionFeeUsdValue
    }

}