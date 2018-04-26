package org.loopring.looprwallet.homemarkets.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_markets_filter.*
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.core.models.markets.MarketsFilter
import org.loopring.looprwallet.core.utilities.FilterButtonUtility

/**
 * Created by Corey on 4/13/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class MarketsFilterViewHolder(itemView: View, private val listener: OnGeneralMarketsFilterChangeListener)
    : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    init {
        val sortByAdapter = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, MarketsFilter.SORT_BY_ARRAY_UI).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        marketsSortBySpinner.adapter = sortByAdapter

        marketsSortBySpinner.onItemSelectedListener = null
        marketsSortBySpinner.setSelection(0)
        marketsSortBySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val value = MarketsFilter.SORT_BY_ARRAY_VALUES[position]
                if (listener.getCurrentSortByFilter() != value) {
                    listener.onSortByChange(value)
                }
            }
        }

        setDateFilterButtonListener(marketsDateFilter1dButton, listener)
    }

    /**
     * @param filter The filter data that's used **ONLY** for binding the current values to
     * the UI (current sortBy status and date)
     */
    fun bind(filter: MarketsFilter) {
        val sortByIndex = MarketsFilter.SORT_BY_ARRAY_VALUES.indexOf(filter.sortBy)
        marketsSortBySpinner.setSelection(sortByIndex)

        // Date
        when (filter.changePeriod) {
            MarketsFilter.CHANGE_PERIOD_1D -> FilterButtonUtility.toSelected(marketsDateFilter1dButton)
            else -> loge("Invalid changePeriod, found: ${filter.changePeriod}", IllegalArgumentException())
        }
    }

    private fun setDateFilterButtonListener(button: Button, listener: OnGeneralMarketsFilterChangeListener?) {
        button.setOnClickListener {
            if (listener?.getCurrentDateFilter() != button.text.toString()) {
                // The user clicked on a different date filter
                listener?.onDateFilterChange(button.text.toString())
            }
        }
    }

}