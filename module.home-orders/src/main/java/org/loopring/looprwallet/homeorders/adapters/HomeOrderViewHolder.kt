package org.loopring.looprwallet.homeorders.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_home_order.*
import org.loopring.looprwallet.core.extensions.formatAsToken
import org.loopring.looprwallet.core.models.loopr.orders.AppLooprOrder
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.homeorders.R
import org.loopring.looprwallet.homeorders.dagger.homeOrdersLooprComponent
import org.loopring.looprwallet.orderdetails.presenters.OrderStatusPresenter
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
class HomeOrderViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), LayoutContainer {

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
    inline fun bind(order: AppLooprOrder, showDateHeader: Boolean, crossinline onOrderClick: (AppLooprOrder) -> Unit, noinline onOrderLongClick: ((String) -> Unit)?) {

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
        viewHolderHomeOrderContainer.setOnClickListener {
            onOrderClick(order)
        }

        onOrderLongClick?.let {listener ->
            viewHolderHomeOrderContainer.setOnLongClickListener {
                listener.invoke(order.orderHash)
                true
            }
        }

        OrderStatusPresenter.bind(viewHolderHomeOrderCompleteImage, viewHolderHomeOrderProgress, order)

        viewHolderHomeOrderTradePairLabel.text = order.tradingPair.market

        if (order.isSell) {
            viewHolderHomeOrderSellCircle.visibility = View.VISIBLE
            viewHolderHomeOrderBuyCircle.visibility = View.GONE
        } else {
            viewHolderHomeOrderBuyCircle.visibility = View.VISIBLE
            viewHolderHomeOrderSellCircle.visibility = View.GONE
        }

        order.tradingPair.secondaryToken.let { token ->
            val quantityText = order.amount.formatAsToken(currencySettings, token)
            viewHolderHomeOrderQuantityLabel.text = str(R.string.formatter_vol).format(quantityText)

            val priceText = order.priceInSecondaryTicker.formatAsToken(currencySettings, token)
            viewHolderHomeOrderPriceLabel.text = priceText
        }

        order.tradingPair.primaryToken.let { token ->
            viewHolderHomeOrderTotalPriceLabel.text = order.totalPrice.formatAsToken(currencySettings, token)
        }
    }

}