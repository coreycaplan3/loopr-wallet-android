package org.loopring.looprwallet.core.models.loopr.orders

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import kotlinx.coroutines.experimental.android.HandlerContext
import org.loopring.looprwallet.core.extensions.toBigDecimal
import org.loopring.looprwallet.core.extensions.toBigInteger
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.loopr.orders.OrderSummaryFilter.Companion.FILTER_OPEN_NEW
import org.loopring.looprwallet.core.models.loopr.orders.OrderSummaryFilter.Companion.FILTER_OPEN_PARTIAL
import org.loopring.looprwallet.core.models.realm.TrackedRealmObject
import org.loopring.looprwallet.core.repositories.loopr.LooprMarketsRepository
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import org.loopring.looprwalletnetwork.models.loopring.responseObjects.LooprOrder as LibraryLooprOrder

/**
 * Created by Corey Caplan on 1/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 * @property orderHash The unique hash of the order
 * @property address The address of the user who created the order
 * @property orderDate The date at which the order was produced
 * @property isSell True if the order is a sell, false for a buy
 * @property tradingPair The [TradingPair] that corresponds to this order
 * @property percentageFilled The percentage filled for the order; between 0 and 100 inclusive
 * @property status The status of the order. See [OrderSummaryFilter.FILTER_OPEN_ALL] and other statuses
 * @property amount The total amount of *primaryTicker* being traded
 * @property priceInSecondaryTicker The total amount of *secondaryTicker* being traded
 * @property priceInSecondaryTicker The price of the order in terms of *secondaryTicker*, IE WETH
 * @property expirationDate The expiration date of the order
 */
open class AppLooprOrder(@PrimaryKey var orderHash: String = "") : RealmObject(), TrackedRealmObject {

    companion object {

        fun convertToLibraryLooprOrder(order: AppLooprOrder) = LibraryLooprOrder().apply {
            this.protocol = order.protocol
            this.delegateAddress = order.delegateAddress
            this.owner = order.address

            val secondaryToken = order.tradingPair.secondaryToken
            if (!order.isSell) {
                // Buying
                this.amtToBuy = order.amount
                this.toBuy = order.tradingPair.primaryTicker

                this.amtToSell = order.priceInSecondaryTicker.toBigInteger(secondaryToken)
                this.toSell = order.tradingPair.secondaryTicker
            } else {
                // Selling
                this.amtToSell = order.amount
                this.toSell = order.tradingPair.primaryTicker

                this.amtToBuy = order.totalPrice.toBigInteger(secondaryToken)
                this.toBuy = order.tradingPair.secondaryTicker
            }

            this.buyNoMoreThanBuyAmt = order.buyNoMoreThanBuyAmount
            this.validSince = order.orderDate
            this.validUntil = order.expirationDate
            this.lrcFee = order.lrcFee
            this.marginSplitPercentage = order.marginSplitPercentage

            this.v = order.v
            this.r = order.r
            this.s = order.s
        }

        /**
         * Converts from [LibraryLooprOrder] to this [AppLooprOrder] object, if possible. If it fails,
         * null is returned and the null reference should be **discarded**.
         *
         * @param order The original order, from the library
         * @param context The thread on which this method is called. Used for retrieving
         * information from realm.
         */
        fun convertFromLibraryLooprOrder(order: LibraryLooprOrder, context: HandlerContext = IO): AppLooprOrder? {
            return AppLooprOrder().apply {
                this.protocol = order.protocol ?: return null
                this.delegateAddress = order.delegateAddress ?: return null
                this.address = order.owner ?: return null

                val repository = LooprMarketsRepository()
                val market = order.market ?: return null

                this.tradingPair = repository.getMarketNow(market, context) ?: return null

                val side = order.side ?: return null
                if ("buy".equals(side, ignoreCase = true)) {
                    // Buying
                    this.amount = order.amtToBuy ?: return null

                    val amountToBuy = order.amtToBuy ?: return null
                    val amountToSell = order.amtToSell ?: return null
                    this.priceInSecondaryTicker = amountToSell.toBigDecimal() / amountToBuy.toBigDecimal()
                } else {
                    // Selling
                    this.amount = order.amtToSell ?: return null

                    val amountToSell = order.amtToSell ?: return null
                    val amountToBuy = order.amtToBuy ?: return null
                    this.priceInSecondaryTicker = amountToBuy.toBigDecimal() / amountToSell.toBigDecimal()
                }

                this.buyNoMoreThanBuyAmount = order.buyNoMoreThanBuyAmt == true
                this.orderDate = order.validSince ?: return null
                this.expirationDate = order.validUntil ?: return null
                this.lrcFee = order.lrcFee ?: return null
                this.marginSplitPercentage = order.marginSplitPercentage ?: return null
            }
        }
    }

    var address: String = ""

    @Index
    var orderDate: Date = Date()

    var isSell: Boolean = false

    var percentageFilled: Int = 100

    var status: String = FILTER_OPEN_NEW

    var expirationDate: Date = Date()

    var buyNoMoreThanBuyAmount: Boolean = true

    var protocol: String = ""

    var delegateAddress: String = ""

    var marginSplitPercentage: Int = 50

    var v: String = ""

    var r: String = ""

    var s: String = ""

    override var lastUpdated: Date = Date()

    var mTradingPair: TradingPair? = null
        private set

    var tradingPair: TradingPair
        get() = mTradingPair!!
        set(value) {
            mTradingPair = value
        }

    private var mAmount: String = BigInteger.ZERO.toString(10)

    var amount: BigInteger
        get() = BigInteger(mAmount)
        set(value) {
            mAmount = value.toString(10)
        }

    val totalPrice: BigDecimal
        get() = priceInSecondaryTicker * amount.toBigDecimal(tradingPair.primaryToken)

    private var mPriceInSecondaryTicker: String = BigDecimal.ZERO.toPlainString()

    /**
     * The price of the ticker in terms of the secondary ticker. For example in LRC-WETH, the price
     * is represented in terms of WETH. No change of the scale or moving of decimal places is
     * necessary.
     */
    var priceInSecondaryTicker: BigDecimal
        get() = BigDecimal(mPriceInSecondaryTicker)
        set(value) {
            mPriceInSecondaryTicker = value.toPlainString()
        }

    /**
     * True if the order is open or false if it's cancelled, filled, or expired
     */
    val isOpen: Boolean
        get() = status == FILTER_OPEN_PARTIAL || status == FILTER_OPEN_NEW

    /**
     * True if the order is complete (filled, cancelled or expired) or false if it's open (new or
     * partially filled)
     */
    val isComplete: Boolean
        get() {
            return status != FILTER_OPEN_PARTIAL && status != FILTER_OPEN_NEW
        }

    private var mLrcFee: String = BigInteger.ZERO.toString(10)

    var lrcFee: BigInteger
        get() = BigInteger(mLrcFee)
        set(value) {
            mLrcFee = value.toString(10)
        }

}