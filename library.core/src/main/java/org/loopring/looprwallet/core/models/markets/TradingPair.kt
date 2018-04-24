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
 * @property high The 24h high price
 * @property low The 24h low price
 * @property amountOfPrimary The amount of [primaryTicker] that was traded in the past 24h
 * @property volumeOfSecondary The volume of [secondaryTicker] that was traded in the past 24h.
 */
open class TradingPair(
        var primaryTicker: String = "",
        var secondaryTicker: String = "",
        var isFavorite: Boolean = false,
        var high: Double = 0.00,
        var low: Double = 0.00,
        var amountOfPrimary: Double = 0.00,
        var volumeOfSecondary: Double = 0.00
) : RealmObject() {

    /**
     * The [primaryTicker] and [secondaryTicker] formatted as *[primaryTicker]-[secondaryTicker]*
     */
    val market: String
        get() = "$primaryTicker-$secondaryTicker"

}