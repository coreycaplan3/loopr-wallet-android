package org.loopring.looprwallet.core.models.loopr.markets

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.loopring.looprwalletnetwork.models.loopring.responseObjects.LooprTicker
import java.math.BigDecimal

/**
 * Created by Corey Caplan on 1/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To represent a trading pair for a token.
 *
 * @property market The market for the ticker, formatted as XXX-XXX (where X is an uppercase letter)
 * @property primaryTokenName The full name of the primary token
 * @property primaryTicker The main ticker for this trading pair. For example the primary ticker for
 * "LRC-WETH" is LRC.
 * @property secondaryTicker The base for this trading pair. For example, the secondary ticker for
 * "LRC-WETH" is WETH.
 * @property isFavorite True if this trading pair is a favorite of the user or false if it's not.
 * @property highPrice The 24h high price
 * @property lowPrice The 24h low price
 * @property amountOfPrimary The amount of [primaryTicker] that was traded in the past 24h
 * @property volumeOfSecondary The volume of [primaryTicker] in relationship with [secondaryTicker]
 * that was traded in the past 24h. IE 10,000 LRC was traded which equated to 100 WETH (secondary)
 */
open class TradingPair(
        @PrimaryKey var market: String = "",
        var primaryTokenName: String? = null,
        looprTicker: LooprTicker? = null
) : RealmObject() {

    companion object {

        fun isValidMarket(market: String) = Regex("[A-Z]{2,7}-[A-Z]{2,7}").matches(market)

    }

    var isFavorite: Boolean = false

    val lastPrice: BigDecimal?
        get() = mLooprTicker?.last

    val change24h: String?
        get() = mLooprTicker?.change

    val highPrice: BigDecimal?
        get() = mLooprTicker?.high

    val lowPrice: BigDecimal?
        get() = mLooprTicker?.low

    val amountOfPrimary: BigDecimal?
        get() = mLooprTicker?.amount

    val volumeOfSecondary: BigDecimal?
        get() = mLooprTicker?.vol

    private var mLooprTicker: LooprTicker? = looprTicker

    val primaryTicker: String
        get() = market.split("-")[0]

    val secondaryTicker: String
        get() = market.split("-")[1]

    init {
        market = market.toUpperCase()
    }

}