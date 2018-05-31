package org.loopring.looprwallet.core.models.loopr.tokens

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import java.math.BigInteger
import java.util.*

/**
 * Created by Corey Caplan on 3/15/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
open class LooprToken(
        @PrimaryKey override var identifier: String = ETH.identifier,

        @Index override var ticker: String = ETH.ticker,

        override var name: String = ETH.name,

        totalSupply: BigInteger = BigInteger.ZERO,

        override var decimalPlaces: Int = 18,

        override var tokenBalances: RealmList<TokenBalanceInfo> = RealmList(),

        override var tokenAllowances: RealmList<TokenAllowanceInfo> = RealmList(),

        override var tokenPrices: RealmList<TokenPriceInfo> = RealmList(),

        override var lastUpdated: Date = Date()
) : RealmObject(), CryptoToken {

    companion object {

        /**
         * A token used to represent ETH. Since ETH isn't a token, this is a special case.
         *
         * This token is automatically added to the user's realm upon creation.
         */
        val ETH: LooprToken
            get() = LooprToken(
                    "ETH",
                    "ETH",
                    "Ethereum",
                    BigInteger("120000000000000000000000000"),
                    18
            )

        val WETH: LooprToken
            get() = LooprToken(
                    "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2",
                    "WETH",
                    "Wrapped ETH",
                    BigInteger("120000000000000000000000000"),
                    18
            )

        val LRC: LooprToken
            get() = LooprToken(
                    "0xef68e7c694f40c8202821edf525de3782458639f",
                    "LRC",
                    "Loopring",
                    BigInteger("1395076054523857892274603100"),
                    18
            )

        val APPC: LooprToken
            get() = LooprToken(
                    "0x1a7a8bd9106f2b8d977e08582dc7d24c723ab0db",
                    "APPC",
                    "AppCoins",
                    BigInteger("246203093000000000000000000"),
                    18
            )

        val REQ: LooprToken
            get() = LooprToken(
                    "0x8f8221afbb33998d8584a2b05749ba73c37a938a",
                    "REQ",
                    "Request Network",
                    BigInteger("999999999244592134526985951"),
                    18
            )

        val ZRX: LooprToken
            get() = LooprToken(
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return identifier == (other as? LooprToken)?.identifier
    }

    override fun hashCode(): Int {
        var result = identifier.hashCode()
        result = 31 * result + ticker.hashCode()
        return result
    }


}