package org.loopring.looprwallet.homeorders.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_open_order_filter.*
import org.loopring.looprwallet.core.models.order.OrderFilter.Companion.FILTER_DATES
import org.loopring.looprwallet.core.models.order.OrderFilter.Companion.FILTER_OPEN_ORDER_STATUS_UI
import org.loopring.looprwallet.core.models.order.OrderFilter.Companion.FILTER_OPEN_ORDER_STATUS_VALUES

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To display the filter options for open order tab.
 */
class GeneralOpenOrderFilterViewHolder(
        itemView: View,
        listener: OnGeneralOrderFilterChangeListener,
        onCancelAllClickListener: ((View) -> Unit)
) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View
        get() = itemView

    init {
        val context = itemView.context
        val layoutRes = android.R.layout.simple_spinner_item
        val dropDownViewRes = android.R.layout.simple_spinner_dropdown_item

        // STATUS SPINNER
        val statusAdapter = ArrayAdapter(context, layoutRes, FILTER_OPEN_ORDER_STATUS_UI).apply {
            setDropDownViewResource(dropDownViewRes)
        }
        openOrderFilterStatusSpinner.adapter = statusAdapter
        openOrderFilterDateSpinner.setSelection(0)
        openOrderFilterStatusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                listener.onStatusFilterChange(FILTER_OPEN_ORDER_STATUS_VALUES[position])
            }
        }


        // DATE SPINNER
        val dateAdapter = ArrayAdapter(context, layoutRes, FILTER_DATES).apply {
            setDropDownViewResource(dropDownViewRes)
        }
        openOrderFilterDateSpinner.adapter = dateAdapter
        openOrderFilterDateSpinner.setSelection(0)
        openOrderFilterDateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                listener.onDateFilterChange(FILTER_DATES[position])
            }
        }

        // CANCEL ALL BUTTON
        openOrderCancelAllButton.setOnClickListener(onCancelAllClickListener)

    }

    /**
     * @param statusRawValue The status ENUM, as seen in the Loopring API for filtering orders by
     * status.
     */
    fun bind(dateValue: String, statusRawValue: String) {
        openOrderFilterDateSpinner.setSelection(FILTER_DATES.indexOf(dateValue))
        openOrderFilterStatusSpinner.setSelection(FILTER_OPEN_ORDER_STATUS_VALUES.indexOf(statusRawValue))
    }

}