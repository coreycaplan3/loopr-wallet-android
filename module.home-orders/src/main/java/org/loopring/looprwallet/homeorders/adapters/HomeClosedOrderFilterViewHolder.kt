package org.loopring.looprwallet.homeorders.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To display the filter options for filled or cancelled order tab.
 */
class HomeClosedOrderFilterViewHolder(itemView: View)
    : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View
        get() = itemView

    init {
//        setButtonClickListener(orderClosedDate1hButton, listener)
//        setButtonClickListener(orderClosedDate1dButton, listener)
//        setButtonClickListener(orderClosedDate1wButton, listener)
//        setButtonClickListener(orderClosedDate1mButton, listener)
//        setButtonClickListener(orderClosedDate1yButton, listener)
//        setButtonClickListener(orderClosedDateAllButton, listener)
    }

    fun bind() {
//        // Reset their UI values
//        FilterButtonUtility.toNormal(orderClosedDate1hButton)
//        FilterButtonUtility.toNormal(orderClosedDate1dButton)
//        FilterButtonUtility.toNormal(orderClosedDate1wButton)
//        FilterButtonUtility.toNormal(orderClosedDate1mButton)
//        FilterButtonUtility.toNormal(orderClosedDate1yButton)
//        FilterButtonUtility.toNormal(orderClosedDateAllButton)
//
//        // Set the selected one's value
//        when (dateValue) {
//            str(R.string._1h) -> FilterButtonUtility.toSelected(orderClosedDate1hButton)
//            str(R.string._1d) -> FilterButtonUtility.toSelected(orderClosedDate1dButton)
//            str(R.string._1w) -> FilterButtonUtility.toSelected(orderClosedDate1wButton)
//            str(R.string._1m) -> FilterButtonUtility.toSelected(orderClosedDate1mButton)
//            str(R.string._1y) -> FilterButtonUtility.toSelected(orderClosedDate1yButton)
//            str(R.string.all) -> FilterButtonUtility.toSelected(orderClosedDateAllButton)
//        }
    }

    // MARK - Private Methods

//    private fun setButtonClickListener(button: Button, listener: OnHomeOrderFilterChangeListener) {
//        button.setOnClickListener {
//            if (listener.getCurrentDateFilter() != button.text.toString()) {
//                 The user clicked on a different date filter change
//                listener.onDateFilterChange(button.text.toString())
//            }
//        }
//    }

}