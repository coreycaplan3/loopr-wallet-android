package org.loopring.looprwallet.core.models.loopr.tokens

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import org.loopring.looprwallet.core.extensions.formatAsToken
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

/**
 * Created by Corey Caplan on 3/15/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 * @property identifier The identifier used to uniquely identify this token. For Ethereum-based
 * tokens, this can be the contract address.
 * @property ticker The ticker for this token. A value of "ETH" means the token is actually ethereum.
 * @property totalSupply The total supply of the crypto. To get the [BigDecimal] version, we must
 * assign this string value to a big decimal and divide it by (10^decimalPlaces). Said differently,
 * this variable should have **NO** decimal places in it.
 * @property priceInUsd The price of the token currently in USD. This number always has a radix of
 * size 10. To get the decimal representation of this number, divide it by 100.
 * @property priceInNativeCurrency The price of the token currently in the user's native currency.
 * This number always has a radix of size 10. To get the decimal representation of this number,
 * divide it by 100.
 * This [BigDecimal] should have a scale of exactly 2.
 */
open class LooprToken(
        @PrimaryKey override var identifier: String = ETH.identifier,

        @Index override var ticker: String = ETH.ticker,

        override var name: String = ETH.name,

        totalSupply: BigInteger = BigInteger.ZERO,

        override var decimalPlaces: Int = ETH.decimalPlaces,

        /**
         * The list of balances mapping to an address owning the balance. If this object was
         * retrieved from the network, the first index is the newly retrieved balance for that
         * user.
         */
        override var tokenBalances: RealmList<TokenBalanceInfo> = RealmList(),

        /**
         * The list of allowances mapping to a wallet address owning having that allowance. If this
         * object was retrieved from the network, the first index is the newly retrieved balance
         * for that user.
         */
        var tokenAllowances: RealmList<TokenAllowanceInfo> = RealmList(),

        priceInUsd: BigInteger? = null,

        @Ignore override var priceInNativeCurrency: BigInteger? = null,

        override var lastUpdated: Date = Date()
) : RealmObject(), CryptoToken {

    companion object {

        /**
         * A token used to represent ETH. Since ETH isn't a token, this is a special case.
         *
         * This token is automatically added to the user's realm upon creation.
         */
        val ETH = LooprToken(
                "ETH",
                "ETH",
                "Ethereum",
                BigInteger("120000000000000000000000000"),
                18
        )

        val WETH = LooprToken(
                "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2",
                "WETH",
                "Wrapped ETH",
                BigInteger("120000000000000000000000000"),
                18
        )

        val LRC = LooprToken(
                "0xef68e7c694f40c8202821edf525de3782458639f",
                "LRC",
                "Loopring",
                BigInteger("1395076054523857892274603100"),
                18
        )

        val APPC = LooprToken(
                "0x1a7a8bd9106f2b8d977e08582dc7d24c723ab0db",
                "APPC",
                "AppCoins",
                BigInteger("246203093000000000000000000"),
                18
        )

        val REQ = LooprToken(
                "0x8f8221afbb33998d8584a2b05749ba73c37a938a",
                "REQ",
                "Request Network",
                BigInteger("999999999244592134526985951"),
                18
        )

        val ZRX = LooprToken(
                "0xe41d2489571d322189246dafa5ebde1f4699f498",
                "ZRX",
                "0x",
                BigInteger("1000000000000000000000000000"),
                18
        )
    }

    override var totalSupply: BigInteger
        get() = BigInteger(mTotalSupply)
        set(value) {
            mTotalSupply = value.toString(10)
        }

    private var mTotalSupply: String = totalSupply.toString(10)

    /**
     * This is a backing field.
     *
     * The price of this token, in USD. This allows for standard conversions to occur later.
     */
    final override var priceInUsd: BigInteger?
        get() = mPriceInUsd?.let { BigInteger(it) }
        set(value) {
            mPriceInUsd = value?.toString(10)
        }

    private var mPriceInUsd: String?

    /**
     * Finds a given address's [TokenAllowanceInfo] or null if it cannot be found
     */
    fun findAddressAllowance(address: String): BigInteger? {
        return tokenAllowances.find { it?.address == address }?.allowance
    }

    init {
        // Needed to set backing fields initially. They will be overwritten by the two calls below
        this.mPriceInUsd = null

        // This will set mPriceInUsd
        this.priceInUsd = priceInUsd
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return identifier == (other as? CryptoToken)?.identifier
    }

    override fun hashCode(): Int {
        var result = identifier.hashCode()
        result = 31 * result + ticker.hashCode()
        return result
    }


}