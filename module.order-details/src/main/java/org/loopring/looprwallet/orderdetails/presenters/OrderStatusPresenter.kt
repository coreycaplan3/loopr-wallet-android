package org.loopring.looprwallet.orderdetails.presenters

import android.view.View
import android.widget.ImageView
import org.loopring.looprwallet.core.models.loopr.orders.AppLooprOrder
import org.loopring.looprwallet.core.models.loopr.orders.OrderSummaryFilter
import org.loopring.looprwallet.orderdetails.R
import org.loopring.looprwallet.orderdetails.views.OrderProgressView

object OrderStatusPresenter {

    fun bind(statusImageView: ImageView, orderProgressView: OrderProgressView, order: AppLooprOrder) {
        when (order.status) {
            OrderSummaryFilter.FILTER_OPEN_NEW -> {
                orderProgressView.visibility = View.VISIBLE
                orderProgressView.progress = 0

                statusImageView.visibility = View.GONE
            }
            OrderSummaryFilter.FILTER_OPEN_PARTIAL -> {
                orderProgressView.visibility = View.VISIBLE
                orderProgressView.progress = order.percentageFilled

                statusImageView.visibility = View.GONE
            }
            OrderSummaryFilter.FILTER_FILLED -> {
                statusImageView.visibility = View.VISIBLE
                statusImageView.setImageResource(R.drawable.ic_swap_horiz_white_24dp)

                orderProgressView.visibility = View.GONE
            }
            OrderSummaryFilter.FILTER_CANCELLED -> {
                statusImageView.visibility = View.VISIBLE
                statusImageView.setImageResource(R.drawable.ic_close_white_24dp)

                orderProgressView.visibility = View.GONE
            }
            OrderSummaryFilter.FILTER_EXPIRED -> {
                statusImageView.visibility = View.VISIBLE
                statusImageView.setImageResource(R.drawable.ic_alarm_white_24dp)

                orderProgressView.visibility = View.GONE
            }
        }
    }

}