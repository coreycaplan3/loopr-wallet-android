package com.caplaninnovations.looprwallet.models.crypto.eth

import com.caplaninnovations.looprwallet.extensions.equalsZero
import com.caplaninnovations.looprwallet.models.crypto.CryptoToken
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import java.math.BigDecimal
import java.util.*

/**
 * Created by Corey Caplan on 3/15/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 * @param contractAddress The address used to locate the contract for this token. A value of "ETH"
 * means the token actually represents ethereum. This is because
 * @param ticker The ticker for this token. A value of "ETH" means the token is actually ethereum.
 * @param totalSupply The total supply of the crypto. To get the [BigDecimal] version, we must
 * assign this string value to a big decimal and divide it by (10^decimalPlaces). Said differently,
 * this variable should have **NO** decimal places in it.
 * @param priceInUsd The price of the token currently in USD. This [BigDecimal] should have a scale
 * of exactly 2.
 * @param priceInNativeCurrency The price of the token currently in the user's native currency.
 * This [BigDecimal] should have a scale of exactly 2.
 */
open class EthToken(
        @PrimaryKey var contractAddress: String = "ETH",

        @Index override var ticker: String = "ETH",

        totalSupply: String = "",

        override var decimalPlaces: Int = 18,

        balance: BigDecimal? = null,

        priceInUsd: BigDecimal? = null,

        @Ignore override var priceInNativeCurrency: BigDecimal? = null,

        override var lastUpdated: Date = Date()
) : RealmObject(), CryptoToken {

    companion object {

        /**
         * A token used to represent ETH. Since ETH isn't a token, this is a special case.
         *
         * This token is automatically added to the user's realm upon creation.
         */
        val ETH = EthToken("ETH", "ETH")

        val LRC = EthToken(
                "0xef68e7c694f40c8202821edf525de3782458639f",
                "LRC",
                "13950760545238578922746031",
                18
        )

        val APPC = EthToken(
                "0x1a7a8bd9106f2b8d977e08582dc7d24c723ab0db",
                "APPC",
                "246203093000000000000000000",
                18
        )

        val REQ = EthToken(
                "0x8f8221afbb33998d8584a2b05749ba73c37a938a",
                "REQ",
                "999999999244592134526985951",
                18
        )
    }

    /**
     * This is a backing field
     */
    override val identifier
        get() = contractAddress

    override var totalSupply: BigDecimal
        get() = BigDecimal(mTotalSupply)
        set(value) {
            val result = if (value.scale() != 0) {
                val multiplier = BigDecimal(10).pow(decimalPlaces - value.scale())
                value * multiplier
            } else {
                value
            }
            mTotalSupply = result.toPlainString()
        }

    private var mTotalSupply: String = totalSupply

    final override var balance: BigDecimal?
        get() = mBalance?.let { BigDecimal(it) }
        set(value) {
            if(value?.equalsZero() == true) {
                mBalance = "0"
            } else {
                mBalance = value?.toPlainString()
            }
        }

    private var mBalance: String?

    /**
     * This is a backing field.
     *
     * The price of this token, in USD. This allows for standard conversions to occur later.
     */
    final override var priceInUsd: BigDecimal?
        get() = mPriceInUsd?.let { BigDecimal(it) }
        set(value) {
            if (value != null && value.scale() != 2) {
                throw IllegalArgumentException("Scale should be set to 2, found ${value.scale()}")
            }

            mPriceInUsd = value?.toPlainString()
        }

    private var mPriceInUsd: String?

    init {
        // needed to set backing fields initially. They will be overwritten by the two calls below
        this.mBalance = null
        this.mPriceInUsd = null

        this.balance = balance // this will set mBalance
        this.priceInUsd = priceInUsd // this will set mPriceInUsd
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return identifier == (other as? CryptoToken)?.identifier
    }

    override fun hashCode(): Int {
        var result = contractAddress.hashCode()
        result = 31 * result + ticker.hashCode()
        return result
    }


}