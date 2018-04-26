package org.loopring.looprwallet.homeorders.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_general_order.*
import org.loopring.looprwallet.core.models.order.LooprOrder
import org.loopring.looprwallet.core.models.order.OrderFilter
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.homeorders.R
import org.loopring.looprwallet.homeorders.dagger.homeOrdersLooprComponent
import java.math.BigDecimal
import java.text.SimpleDateFormat
import javax.inject.Inject

/**
 * Created by Corey Caplan on 4/7/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class GeneralOrderViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    @Inject
    lateinit var currencySettings: CurrencySettings

    init {
        homeOrdersLooprComponent.inject(this)
    }

    /**
     * Binds the order to this ViewHolder and registers a click listener to the itemView.
     *
     * @param order The order to be bound to this ViewHolder
     * @param showDateHeader True to show the date header for this order or false to hide it
     * @param onOrderClick A click handler that is fired when a user clicks on an order
     */
    @SuppressLint("SetTextI18n")
    inline fun bind(order: LooprOrder, showDateHeader: Boolean, crossinline onOrderClick: (LooprOrder) -> Unit) {

        // Date Title
        when (showDateHeader) {
            true -> {
                generalOrderDateTitle.visibility = View.VISIBLE

                val locale = currencySettings.getCurrentLocale()
                generalOrderDateTitle.text = SimpleDateFormat("MMM d, yyyy", locale).format(order.orderDate)
            }
            else -> {
                generalOrderDateTitle.visibility = View.GONE
            }
        }

        // The container
        generalOrderContainer.setOnClickListener {
            onOrderClick(order)
        }

        when (order.status) {
            OrderFilter.FILTER_OPEN_NEW -> {
                generalOrderProgress.visibility = View.VISIBLE
                generalOrderProgress.progress = 0

                generalOrderCompleteImage.visibility = View.GONE
            }
            OrderFilter.FILTER_OPEN_PARTIAL -> {
                generalOrderProgress.visibility = View.VISIBLE
                generalOrderProgress.progress = order.percentageFilled

                generalOrderCompleteImage.visibility = View.GONE
            }
            OrderFilter.FILTER_FILLED -> {
                generalOrderCompleteImage.visibility = View.VISIBLE
                generalOrderCompleteImage.setImageResource(R.drawable.ic_swap_horiz_white_24dp)

                generalOrderProgress.visibility = View.GONE
            }
            OrderFilter.FILTER_CANCELLED -> {
                generalOrderCompleteImage.visibility = View.VISIBLE
                generalOrderCompleteImage.setImageResource(R.drawable.ic_cancel_white_24dp)

                generalOrderProgress.visibility = View.GONE
            }
            OrderFilter.FILTER_EXPIRED -> {
                generalOrderCompleteImage.visibility = View.VISIBLE
                generalOrderCompleteImage.setImageResource(R.drawable.ic_alarm_white_24dp)

                generalOrderProgress.visibility = View.GONE
            }
        }

        generalOrderProgress.visibility = View.GONE

        genericOrderTradePairLabel.text = order.tradingPair.market

        if (order.isSell) {
            generalOrderSellCircle.visibility = View.VISIBLE
            generalOrderBuyCircle.visibility = View.GONE
        } else {
            generalOrderBuyCircle.visibility = View.VISIBLE
            generalOrderSellCircle.visibility = View.GONE
        }

        val tokenFormatter = currencySettings.getNumberFormatter()

        val quantityText = "${tokenFormatter.format(order.amount)} ${order.tradingPair.secondaryTicker}"
        val formatter = str(R.string.formatter_vol)
        genericOrderQuantityLabel.text = formatter.format(quantityText)

        genericOrderPriceFiatLabel.text = "${tokenFormatter.format(order.priceInSecondaryTicker)} ${order.tradingPair.secondaryTicker}"

        val currencyFormatter = currencySettings.getCurrencyFormatter()
        genericOrderPriceFiatLabel.text = currencyFormatter.format(order.priceInUsd)

        genericOrderPriceSecondaryTokenLabel.text = tokenFormatter.format(order.priceInSecondaryTicker)

        val totalPriceInFiat = BigDecimal(order.priceInUsd) * BigDecimal(order.amount)
        genericOrderTotalPriceLabel.text = currencyFormatter.format(totalPriceInFiat)
    }

}