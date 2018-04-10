package org.loopring.looprwallet.homeorders.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_closed_order_filter.*

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To display the filter options for closed order tab.
 */
class GeneralClosedOrderFilterViewHolder(
        itemView: View,
        listener: OnGeneralOrderFilterActionListener
) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View
        get() = itemView

    init {
        val context = itemView.context
        val layoutRes = android.R.layout.simple_spinner_item

        // DATE SPINNER
        closedOrderFilterDateSpinner.adapter = ArrayAdapter(context, layoutRes, GeneralOrderAdapter.FILTER_DATES)
        closedOrderFilterDateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                listener.onDateFilterChange(GeneralOrderAdapter.FILTER_DATES[position])
            }
        }

        // STATUS SPINNER
        closedOrderFilterStatusSpinner.adapter = ArrayAdapter(context, layoutRes, GeneralOrderAdapter.FILTER_CLOSED_ORDER_STATUS)
        closedOrderFilterStatusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                listener.onStatusFilterChange(GeneralOrderAdapter.FILTER_CLOSED_ORDER_STATUS[position])
            }
        }
    }

    fun bind(dateValue: String, statusValue: String) {
        closedOrderFilterDateSpinner.setSelection(GeneralOrderAdapter.FILTER_DATES.indexOf(dateValue))
        closedOrderFilterStatusSpinner.setSelection(GeneralOrderAdapter.FILTER_CLOSED_ORDER_STATUS.indexOf(statusValue))
    }

}