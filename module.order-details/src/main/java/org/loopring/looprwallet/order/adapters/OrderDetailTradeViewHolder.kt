package org.loopring.looprwallet.order.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_order_details_fill.*
import org.loopring.looprwallet.core.models.order.LooprOrderFill
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.order.R
import org.loopring.looprwallet.order.dagger.orderDetailsLooprComponent
import javax.inject.Inject

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class OrderDetailTradeViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    @Inject
    lateinit var currencySettings: CurrencySettings

    init {
        orderDetailsLooprComponent.inject(this)
    }

    override val containerView: View?
        get() = itemView

    /**
     * @param index The index of the item in the [RecyclerView]. Should start at 1 (since the
     * summary is at 0)
     * @param orderFill The order fill for this specific trade
     */
    fun bind(index: Int, orderFill: LooprOrderFill) {
        orderDetailsTradeCounterLabel.text = str(R.string.formatter_trade_number).format(index)

        val formatter = currencySettings.getNumberFormatter()
        orderDetailsTradeFillAmountLabel.text = formatter.format(orderFill.fillAmount)

        orderDetailsTradeIdLabel.text = orderFill.orderHash
    }

}