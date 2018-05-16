package org.loopring.looprwallet.orderdetails.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_order_details_summary.*
import org.loopring.looprwallet.core.extensions.formatAsToken
import org.loopring.looprwallet.core.extensions.formatAsTokenNoTicker
import org.loopring.looprwallet.core.extensions.toBigDecimal
import org.loopring.looprwallet.core.extensions.getResourceIdFromAttrId
import org.loopring.looprwallet.core.models.loopr.orders.AppLooprOrder
import org.loopring.looprwallet.core.models.loopr.orders.OrderSummaryFilter
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.utilities.ApplicationUtility.col
import org.loopring.looprwallet.orderdetails.R
import org.loopring.looprwallet.orderdetails.dagger.orderDetailsLooprComponent
import java.math.BigDecimal
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
    fun bind(order: AppLooprOrder) {
        val totalQuantityWithTicker = order.amount.formatAsToken(currencySettings, order.tradingPair.primaryToken)
        if (order.percentageFilled == 100) {
            orderDetailsFilledAmountLabel.text = totalQuantityWithTicker
        } else {
            order.tradingPair.primaryToken.let { token ->
                val amountFilled = BigDecimal(order.percentageFilled / 100.0) * order.amount.toBigDecimal(token)

                val formattedAmountFilled = amountFilled.formatAsTokenNoTicker(currencySettings)
                orderDetailsFilledAmountLabel.text = "$formattedAmountFilled / $totalQuantityWithTicker"
            }
        }


        when (order.status) {
            OrderSummaryFilter.FILTER_OPEN_NEW -> {
                val textColor = itemView.context.theme.getResourceIdFromAttrId(android.R.attr.textColorPrimary)
                orderDetailsStatusLabel.setTextColor(col(textColor))
                orderDetailsStatusLabel.setText(R.string._new)
            }
            OrderSummaryFilter.FILTER_OPEN_PARTIAL -> {
                orderDetailsStatusLabel.setTextColor(col(R.color.amber_500))
                orderDetailsStatusLabel.setText(R.string.partial)
            }
            OrderSummaryFilter.FILTER_FILLED -> {
                orderDetailsStatusLabel.setTextColor(col(R.color.green_400))
                orderDetailsStatusLabel.setText(R.string.filled)
            }
            OrderSummaryFilter.FILTER_CANCELLED -> {
                orderDetailsStatusLabel.setTextColor(col(R.color.red_400))
                orderDetailsStatusLabel.setText(R.string.cancelled)
            }
            OrderSummaryFilter.FILTER_EXPIRED -> {
                orderDetailsStatusLabel.setTextColor(col(R.color.red_400))
                orderDetailsStatusLabel.setText(R.string.expired)
            }
        }

        orderDetailsTotalLabel.text = order.totalPrice.formatAsToken(currencySettings, order.tradingPair.secondaryToken)
    }

}