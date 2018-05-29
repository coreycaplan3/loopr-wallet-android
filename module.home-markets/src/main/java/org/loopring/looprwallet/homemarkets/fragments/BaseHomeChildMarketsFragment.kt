package org.loopring.looprwallet.homemarkets.fragments

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import io.realm.Sort
import org.loopring.looprwallet.core.adapters.LooprLayoutManager
import org.loopring.looprwallet.core.extensions.sort
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairFilter
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter.BottomNavigationReselectedLister
import org.loopring.looprwallet.core.presenters.SearchViewPresenter.OnSearchViewChangeListener
import org.loopring.looprwallet.core.repositories.loopr.LooprMarketsRepository
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.homemarkets.adapters.HomeMarketsAdapter
import org.loopring.looprwallet.homemarkets.adapters.OnGeneralMarketsFilterChangeListener
import org.loopring.looprwallet.core.viewmodels.loopr.MarketsViewModel

/**
 * Created by Corey Caplan on 4/13/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A class that enables the sharing of functionality across [HomeAllMarketsFragment]
 * and [HomeFavoriteMarketsFragment].
 *
 */
abstract class BaseHomeChildMarketsFragment : BaseFragment(), BottomNavigationReselectedLister,
        OnSearchViewChangeListener, OnGeneralMarketsFilterChangeListener {

    abstract val swipeRefreshLayout: SwipeRefreshLayout?

    abstract val recyclerView: RecyclerView?

    /**
     * True if this fragment is showing favorites or false otherwise
     */
    abstract val isFavorites: Boolean

    private val adapter: HomeMarketsAdapter by lazy {
        HomeMarketsAdapter(this, this, isFavorites)
    }

    private val homeMarketsViewModel by lazy {
        LooprViewModelFactory.get<MarketsViewModel>(activity!!, "markets-$isFavorites")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter.onRestoreInstance(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView?.adapter = adapter

        recyclerView?.layoutManager = LooprLayoutManager(view.context)

        setupOfflineFirstStateAndErrorObserver(homeMarketsViewModel, swipeRefreshLayout)
        setMarketsLiveData()
    }

    final override fun onBottomNavigationReselected() {
        recyclerView?.smoothScrollToPosition(0)
    }

    final override fun onQueryTextGainFocus() {
        // NO OP
    }

    final override fun onSearchItemExpanded() {
    }

    final override fun onSearchItemCollapsed() {
        adapter.clearFilter()
        adapter.sortBy.let { onSortByChange(it) }
    }

    final override fun onQueryTextChangeListener(searchQuery: String) {
        val repository = LooprMarketsRepository()
        adapter.pager.data?.let {
            adapter.filterData(repository.filterMarketsByQuery(searchQuery, it))
        }
    }

    final override fun onSortByChange(newSortByFilter: String) {
        homeMarketsViewModel.onSortByChange(newSortByFilter)
        val newData = adapter.pager.data?.where()?.apply {
            when (newSortByFilter) {
                TradingPairFilter.SORT_BY_TICKER_ASC -> sort(TradingPair::market)
                TradingPairFilter.SORT_BY_GAINERS -> sort(TradingPair::change24hAsNumber, Sort.DESCENDING)
                TradingPairFilter.SORT_BY_LOSERS -> sort(TradingPair::change24hAsNumber, Sort.ASCENDING)
            }
        }?.findAllAsync()
        adapter.updateData(newData)
    }

    final override fun onDateFilterChange(newDateFilter: String) {
        setMarketsLiveData()
    }

    final override fun getCurrentDateFilter(): String {
        return adapter.dateFilter
    }

    final override fun getCurrentSortByFilter(): String {
        return adapter.sortBy
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        adapter.onSaveInstanceState(outState)
    }

    // MARK - Protected Methods

    /**
     * Sets the [adapter] to to use a new data set, based on the filter criteria that was
     * provided by the [adapter].
     */
    private fun setMarketsLiveData() {
        val marketsFilter = TradingPairFilter(isFavorites, adapter.dateFilter)
        homeMarketsViewModel.getHomeMarkets(this, marketsFilter, adapter.sortBy) { data ->
            setupOfflineFirstDataObserverForAdapter(homeMarketsViewModel, adapter, data)
        }
    }

}