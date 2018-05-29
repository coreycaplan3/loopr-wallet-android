package org.loopring.looprwallet.orderdetails.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import io.realm.OrderedCollectionChangeSet
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_order_details_fill_empty.*
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter
import org.loopring.looprwallet.core.models.loopr.orders.AppLooprOrder
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrderFill
import org.loopring.looprwallet.core.models.loopr.orders.OrderFillsLooprPager
import org.loopring.looprwallet.core.models.loopr.orders.OrderSummaryFilter.Companion.FILTER_CANCELLED
import org.loopring.looprwallet.core.models.loopr.orders.OrderSummaryFilter.Companion.FILTER_OPEN_ALL
import org.loopring.looprwallet.core.models.loopr.orders.OrderSummaryFilter.Companion.FILTER_OPEN_NEW
import org.loopring.looprwallet.core.models.loopr.orders.OrderSummaryFilter.Companion.FILTER_OPEN_PARTIAL
import org.loopring.looprwallet.core.models.loopr.paging.LooprAdapterPager
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.orderdetails.R

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
class OrderDetailsAdapter(orderSummary: AppLooprOrder) : BaseRealmAdapter<LooprOrderFill>() {

    override var pager: LooprAdapterPager<LooprOrderFill> = OrderFillsLooprPager()

    var orderSummary: AppLooprOrder = orderSummary
        set(value) {
            field = value
            notifyItemChanged(0)
        }

    init {
        containsHeader = true
    }

    override fun onCreateEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = getInflater(parent).inflate(R.layout.view_holder_order_details_fill_empty, parent, false)
        return EmptyTradeViewHolder(view)
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = getInflater(parent)
        val view = inflater.inflate(R.layout.view_holder_order_details_summary, parent, false)
        return OrderSummaryViewHolder(view)
    }

    override fun onCreateDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = getInflater(parent)
        val view = inflater.inflate(R.layout.view_holder_order_details_fill, parent, false)
        return OrderFillViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, index: Int, item: LooprOrderFill?) {
        (holder as? EmptyTradeViewHolder)?.bind(orderSummary.status)

        (holder as? OrderSummaryViewHolder)?.bind(orderSummary)

        item ?: return
        (holder as? OrderFillViewHolder)?.bind(index, item)
    }

    private class EmptyTradeViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), LayoutContainer {

        override val containerView: View?
            get() = itemView

        fun bind(orderStatus: String) {
            val text = when (orderStatus) {
                FILTER_OPEN_ALL, FILTER_OPEN_PARTIAL, FILTER_OPEN_NEW -> str(R.string.fills_empty_open)
                FILTER_CANCELLED -> str(R.string.fills_empty_cancelled)
                else -> null
            }
            orderDetailsEmptyLabel.text = text
        }

    }

}