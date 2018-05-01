package org.loopring.looprwallet.orderdetails.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.LinearLayout
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter
import org.loopring.looprwallet.core.extensions.guard
import org.loopring.looprwallet.core.extensions.inflate
import org.loopring.looprwallet.core.models.order.LooprOrder
import org.loopring.looprwallet.core.models.order.LooprOrderFill
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
class OrderDetailsAdapter(orderSummary: LooprOrder) : BaseRealmAdapter<LooprOrderFill>() {

    init {
        containsHeader = true
    }

    var orderSummary: LooprOrder = orderSummary
        set(value) {
            field = value
            notifyItemChanged(0)
        }

    override val totalItems: Int?
        get() = data?.size ?: 3 // TODO

    init {
        containsHeader = true
    }

    override fun onCreateEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(LinearLayout(parent.context)) {}
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return OrderSummaryViewHolder(parent.inflate(R.layout.view_holder_order_details_summary))
    }

    override fun onCreateDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return OrderFillViewHolder(parent.inflate(R.layout.view_holder_order_details_fill))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, index: Int, item: LooprOrderFill?) {
        (holder as? OrderSummaryViewHolder)?.bind(orderSummary)

        item?.guard {} ?: return
        (holder as? OrderFillViewHolder)?.bind(index, item)
    }

}