package org.loopring.looprwallet.core.models.markets

import io.realm.RealmObject

/**
 * Created by Corey Caplan on 1/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To represent a trading pair for a token.
 *
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
        var primaryTicker: String = "",
        var secondaryTicker: String = "",
        var isFavorite: Boolean = false,
        var lastPrice: Double = 0.00,
        var change24h: String = "0.00%",
        var highPrice: Double = 0.00,
        var lowPrice: Double = 0.00,
        var amountOfPrimary: Double = 0.00,
        var volumeOfSecondary: Double = 0.00
) : RealmObject() {

    companion object {

        fun isValidMarket(market: String) = Regex("[A-Z]{2,7}-[A-Z]{2,7}").matches(market)

        fun createFromMarket(market: String): TradingPair {
            val splitMarket = market.split(Regex("-"))
            val primaryTicker = splitMarket[0]
            val secondaryTradingPair = splitMarket[1]
            return TradingPair(primaryTicker, secondaryTradingPair)
        }

    }

    /**
     * The [primaryTicker] and [secondaryTicker] formatted as *[primaryTicker]-[secondaryTicker]*
     */
    val market: String
        get() = "$primaryTicker-$secondaryTicker"

}