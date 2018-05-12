package org.loopring.looprwallet.core.models.loopr.orders

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.realm.TrackedRealmObject
import org.loopring.looprwallet.core.models.loopr.orders.OrderFilter.Companion.FILTER_OPEN_NEW
import org.loopring.looprwallet.core.models.loopr.orders.OrderFilter.Companion.FILTER_OPEN_PARTIAL
import java.util.*

/**
 * Created by Corey Caplan on 1/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
open class LooprOrder(
        @PrimaryKey var orderHash: String = "",
        var address: String = "",
        @Index var orderDate: Date = Date(),
        var isSell: Boolean = false,
        tradingPair: TradingPair? = null,
        var percentageFilled: Int = 100,
        var status: String = FILTER_OPEN_NEW,
        var amount: Double = 1320.25,
        var total: Double = 10.0,
        var priceInUsd: Double = 0.85,
        var priceInSecondaryTicker: Double = 0.001214,
        var expirationDate: Date = Date(),
        override var lastUpdated: Date = Date()
) : RealmObject(), TrackedRealmObject {

    private var mTradingPair: TradingPair? = null

    /**
     * True if the order is complete (filled, cancelled or expired) or false if it's open (new or
     * partially filled)
     */
    val isComplete: Boolean
        get() {
            return status != FILTER_OPEN_PARTIAL && status != FILTER_OPEN_NEW
        }

    var tradingPair: TradingPair
        get() = mTradingPair!!
        set(value) {
            mTradingPair = value
        }

    init {
        mTradingPair = tradingPair
    }

}