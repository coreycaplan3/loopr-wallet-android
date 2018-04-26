package org.loopring.looprwallet.homemarkets.adapters

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter
import org.loopring.looprwallet.core.extensions.guard
import org.loopring.looprwallet.core.extensions.inflate
import org.loopring.looprwallet.core.extensions.weakReference
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.markets.MarketsFilter
import org.loopring.looprwallet.core.models.markets.TradingPair
import org.loopring.looprwallet.homemarkets.R
import org.loopring.looprwallet.tradedetails.activities.TradingPairDetailsActivity

/**
 * Created by Corey Caplan on 1/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 * @param onRefresh A function that's invoked if the user needs to refresh the data. This only
 * occurs when the market data is empty (fails to load and the Realm is empty) and the user prompts
 * to retry loading the data.
 */
class HomeMarketsAdapter(
        savedInstanceState: Bundle?,
        fragment: BaseFragment,
        listener: OnGeneralMarketsFilterChangeListener,
        onRefresh: () -> Unit
) : BaseRealmAdapter<TradingPair>(), OnGeneralMarketsFilterChangeListener {

    companion object {
        private const val KEY_SORT_BY = "_SORT_BY"
        private const val KEY_DATE_FILTER = "_DATE_FILTER"
    }

    private val fragment by weakReference(fragment)
    private val listener by weakReference(listener)
    private val onRefresh by weakReference(onRefresh)

    var sortBy: String
        private set

    var dateFilter: String
        private set

    override val totalItems: Int?
        get() = null

    init {
        containsHeader = true
        sortBy = savedInstanceState?.getString(KEY_SORT_BY) ?: MarketsFilter.SORT_BY_TICKER_ASC
        dateFilter = savedInstanceState?.getString(KEY_DATE_FILTER) ?: MarketsFilter.CHANGE_PERIOD_1D
    }

    override fun onCreateEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return MarketsEmptyViewHolder(parent.inflate(R.layout.view_holder_markets_empty)) {
            onRefresh?.invoke()
        }
    }

    override fun onCreateDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return MarketsViewHolder(parent.inflate(R.layout.view_holder_markets), ::onTradingPairClick)
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return MarketsFilterViewHolder(parent.inflate(R.layout.view_holder_markets_filter), this)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, index: Int, item: TradingPair?) {
        val filter = MarketsFilter(null, false, sortBy, dateFilter)
        (holder as? MarketsFilterViewHolder)?.let {
            it.bind(filter)
            return
        }

        item?.guard { } ?: return
        (holder as? MarketsViewHolder)?.bind(item)
    }

    override fun onSortByChange(newSortByFilter: String) {
        if (newSortByFilter != sortBy) {
            sortBy = newSortByFilter
            notifyItemChanged(0)
            listener?.onSortByChange(newSortByFilter)
        }
    }

    override fun onDateFilterChange(newDateFilter: String) {
        if (newDateFilter != dateFilter) {
            dateFilter = newDateFilter
            notifyItemChanged(0)
            listener?.onDateFilterChange(newDateFilter)
        }
    }

    override fun getCurrentDateFilter(): String = dateFilter

    override fun getCurrentSortByFilter(): String = sortBy

    fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_SORT_BY, sortBy)
        outState.putString(KEY_DATE_FILTER, dateFilter)
    }

    // MARK - Private Methods

    private fun onTradingPairClick(index: Int) {
        val position = index + dataOffsetPosition
        val tradingPair = data?.get(position) ?: return

        fragment?.let {
            TradingPairDetailsActivity.route(tradingPair, it)
        }
    }

}