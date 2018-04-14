package org.loopring.looprwallet.homemarkets.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_markets_filter.*
import org.loopring.looprwallet.core.models.markets.MarketsFilter

/**
 * Created by Corey on 4/13/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
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

    fun bind(marketsFilter: MarketsFilter) {
        TODO("IMPLEMENT ME")

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