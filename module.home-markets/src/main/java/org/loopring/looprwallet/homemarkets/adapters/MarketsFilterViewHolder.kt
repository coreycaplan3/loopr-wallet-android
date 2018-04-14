package org.loopring.looprwallet.homemarkets.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_markets_filter.*
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.core.models.markets.MarketsFilter
import org.loopring.looprwallet.homemarkets.R

/**
 * Created by Corey on 4/13/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class MarketsFilterViewHolder(itemView: View?, listener: OnGeneralMarketsFilterChangeListener)
    : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    init {
        marketsSortBySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                listener.onSortByChange(MarketsFilter.SORT_BY_ARRAY_VALUES[position])
            }
        }

        setDateFilterButtonListener(marketsDateFilter1dButton, listener)
    }

    /**
     * @param marketsFilter The filter data that's used **ONLY** for binding the current values to
     * the UI (current sortBy status and date)
     */
    fun bind(marketsFilter: MarketsFilter) {
        val spinnerIndex = MarketsFilter.SORT_BY_ARRAY_VALUES.indexOf(marketsFilter.sortBy)
        marketsSortBySpinner.setSelection(spinnerIndex)

        when (marketsFilter.changePeriod) {
            MarketsFilter.CHANGE_PERIOD_1D -> marketsDateFilter1dButton.context.setTheme(R.style.App_Button)
            else -> loge("Invalid changePeriod, found: ${marketsFilter.changePeriod}", IllegalArgumentException())
        }
    }

    private fun setDateFilterButtonListener(button: Button, listener: OnGeneralMarketsFilterChangeListener) {
        button.setOnClickListener {
            if (listener.getCurrentDateFilter() != button.text.toString()) {
                // The user clicked on a different date filter
                listener.onDateFilterChange(button.text.toString())
            }
        }
    }

}