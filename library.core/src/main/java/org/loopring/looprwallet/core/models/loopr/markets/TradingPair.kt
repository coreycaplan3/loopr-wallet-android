package org.loopring.looprwallet.core.models.loopr.markets

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwalletnetwork.models.loopring.responseObjects.LooprTicker
import java.math.BigDecimal

/**
 * Created by Corey Caplan on 1/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To represent a trading pair for a token.
 *
 * @property market The market for the ticker, formatted as ABC-DEF (ALL UPPERCASE LETTERS)
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
        var looprTicker: LooprTicker? = null,
        primaryToken: LooprToken? = null,
        secondaryToken: LooprToken? = null
) : RealmObject() {

    companion object {

        fun isValidMarket(market: String) = Regex("[A-Z]{2,7}-[A-Z]{2,7}").matches(market)

    }

    var isFavorite: Boolean = false

    var changeAsNumber: Double = looprTicker?.change?.let {
        val index = it.indexOf('%')
        StringBuilder(it).deleteCharAt(index).toString().toDouble()
    } ?: 0.00

    val lastPrice: BigDecimal?
        get() = looprTicker?.last

    val change24h: String?
        get() = looprTicker?.change

    val highPrice: BigDecimal?
        get() = looprTicker?.high

    val lowPrice: BigDecimal?
        get() = looprTicker?.low

    val amountOfPrimary: BigDecimal?
        get() = looprTicker?.amount

    val volumeOfSecondary: BigDecimal?
        get() = looprTicker?.vol

    val primaryTicker: String
        get() = market.split("-")[0]

    val secondaryTicker: String
        get() = market.split("-")[1]

    private var mPrimaryToken = primaryToken

    var primaryToken: LooprToken
        get() = mPrimaryToken!!
        set(value) {
            mPrimaryToken = primaryToken
        }

    private var mSecondaryToken = secondaryToken

    var secondaryToken: LooprToken
        get() = mSecondaryToken!!
        set(value) {
            mSecondaryToken = secondaryToken
        }

    init {
        market = market.toUpperCase()
    }

}