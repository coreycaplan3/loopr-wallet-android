package org.loopring.looprwallet.orderdetails.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_order_details_summary.*
import org.loopring.looprwallet.core.extensions.getResourceIdFromAttrId
import org.loopring.looprwallet.core.models.order.LooprOrder
import org.loopring.looprwallet.core.models.order.OrderFilter
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.utilities.ApplicationUtility.col
import org.loopring.looprwallet.orderdetails.R
import org.loopring.looprwallet.orderdetails.dagger.orderDetailsLooprComponent
import javax.inject.Inject

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
class OrderSummaryViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    @Inject
    lateinit var currencySettings: CurrencySettings

    init {
        orderDetailsLooprComponent.inject(this)
    }

    @SuppressLint("SetTextI18n")
    fun bind(order: LooprOrder) {
        val numberFormatter = currencySettings.getNumberFormatter()

        val totalQuantity = numberFormatter.format(order.amount)
        val ticker = order.tradingPair.primaryTicker
        if (order.percentageFilled == 100) {
            orderDetailsFilledAmountLabel.text = "$totalQuantity $ticker"
        } else {
            val amountFilled = (order.percentageFilled / 100.0) * order.amount
            val formattedAmountFilled = numberFormatter.format(amountFilled)
            orderDetailsFilledAmountLabel.text = "$formattedAmountFilled /$totalQuantity $ticker"
        }


        when (order.status) {
            OrderFilter.FILTER_OPEN_NEW -> {
                val textColor = itemView.context.theme.getResourceIdFromAttrId(android.R.attr.textColorPrimary)
                orderDetailsStatusLabel.setTextColor(col(textColor))
                orderDetailsStatusLabel.setText(R.string._new)
            }
            OrderFilter.FILTER_OPEN_PARTIAL -> {
                orderDetailsStatusLabel.setTextColor(col(R.color.amber_500))
                orderDetailsStatusLabel.setText(R.string.partial)
            }
            OrderFilter.FILTER_FILLED -> {
                orderDetailsStatusLabel.setTextColor(col(R.color.green_400))
                orderDetailsStatusLabel.setText(R.string.filled)
            }
            OrderFilter.FILTER_CANCELLED -> {
                orderDetailsStatusLabel.setTextColor(col(R.color.red_400))
                orderDetailsStatusLabel.setText(R.string.cancelled)
            }
            OrderFilter.FILTER_EXPIRED -> {
                orderDetailsStatusLabel.setTextColor(col(R.color.red_400))
                orderDetailsStatusLabel.setText(R.string.expired)
            }
        }

        val secondaryTicker = order.tradingPair.secondaryTicker
        orderDetailsTotalLabel.text = "${numberFormatter.format(order.total)} $secondaryTicker"
    }

}