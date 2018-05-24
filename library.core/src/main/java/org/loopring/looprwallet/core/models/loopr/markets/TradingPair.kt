package org.loopring.looprwallet.core.models.loopr.markets

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
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
open class TradingPair(@PrimaryKey var market: String = "") : RealmObject() {

    var isFavorite: Boolean = false

    private var mLastPrice: String = "0"

    var lastPrice: BigDecimal
        get() = BigDecimal(mLastPrice)
        set(value) {
            mLastPrice = value.toPlainString()
        }

    private var mHighPrice: String = "0"

    var highPrice: BigDecimal
        get() = BigDecimal(mHighPrice)
        set(value) {
            mHighPrice = value.toPlainString()
        }

    private var mLowPrice: String = "0"

    var lowPrice: BigDecimal
        get() = BigDecimal(mLowPrice)
        set(value) {
            mLowPrice = value.toPlainString()
        }

    private var mAmountOfPrimary: String = "0"

    var amountOfPrimary: BigDecimal
        get() = BigDecimal(mAmountOfPrimary)
        set(value) {
            mAmountOfPrimary = value.toPlainString()
        }

    private var mVolumeOfSecondary: String = "0"

    var volumeOfSecondary: BigDecimal
        get() = BigDecimal(mVolumeOfSecondary)
        set(value) {
            mAmountOfPrimary = value.toPlainString()
        }

    var change24h: String = "0.00%"
        set(value) {
            field = value
            change24hAsNumber = convertPercentageChangeToNumber(value)
        }

    var change24hAsNumber: Double = convertPercentageChangeToNumber("0.00%")
        protected set

    val primaryTicker: String
        get() = market.split("-")[0]

    val secondaryTicker: String
        get() = market.split("-")[1]

    private var mPrimaryToken: LooprToken? = null

    var primaryToken: LooprToken
        get() = mPrimaryToken!!
        set(value) {
            mPrimaryToken = value
        }

    private var mSecondaryToken: LooprToken? = null

    var secondaryToken: LooprToken
        get() = mSecondaryToken!!
        set(value) {
            mSecondaryToken = value
        }

    // MARK - Private Methods

    private fun convertPercentageChangeToNumber(change: String): Double {
        val index = change.indexOf("%")
        if (index == -1) {
            return 0.00
        }

        return StringBuilder(change)
                .deleteCharAt(index)
                .toString()
                .trim()
                .toDouble()
    }

}