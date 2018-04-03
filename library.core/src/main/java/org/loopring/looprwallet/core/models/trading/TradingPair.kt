package org.loopring.looprwallet.core.models.trading

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Corey Caplan on 1/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To represent a trading pair for a token.
 *
 */
open class TradingPair(
        @PrimaryKey var ticker: String = ""
) : RealmObject()