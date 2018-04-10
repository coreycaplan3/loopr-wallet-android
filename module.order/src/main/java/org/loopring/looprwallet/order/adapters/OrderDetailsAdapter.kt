package org.loopring.looprwallet.order.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import io.realm.RealmModel
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter
import org.loopring.looprwallet.core.extensions.inflate
import org.loopring.looprwallet.order.R

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
class OrderDetailsAdapter(private val orderSummary: RealmModel) : BaseRealmAdapter<RealmModel>() {

    companion object {
        /**
         * The first item in the Adapter - The summary of the order
         */
        const val TYPE_SUMMARY = 3
    }

    override val totalItems: Int?
        get() = TODO("ADD ONCE ORDERS OBJECT IS AVAILABLE")

    init {
        extraDataToPositionList = listOf(orderSummary to 0)
    }

    override fun getItemViewType(position: Int): Int {
        val viewType = super.getItemViewType(position)
        return when (viewType) {
            TYPE_DATA -> when (position) {
                0 -> TYPE_SUMMARY
                else -> TYPE_DATA
            }
            else -> viewType
        }
    }

    override fun onCreateEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        throw NotImplementedError("This method should never be called, since we already had SOME " +
                "data before reaching this adapter")
    }

    override fun onCreateDataViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_SUMMARY -> OrderSummaryViewHolder(parent.inflate(R.layout.view_holder_order_details_summary))
        TYPE_DATA -> OrderSummaryViewHolder(parent.inflate(R.layout.view_holder_order_details_fill))
        else -> throw IllegalArgumentException("Invalid viewType, found $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, index: Int, item: RealmModel) {
        (holder as? OrderSummaryViewHolder)?.bind(orderSummary)
        (holder as? OrderDetailTradeViewHolder)?.bind(item)
    }

    override fun getItemCount(): Int {
        return 1 + (data?.let { getItemCountForOnlyData(it) } ?: 0)
    }
}