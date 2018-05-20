package org.loopring.looprwallet.homemarkets.fragments

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.extensions.ifNotNull
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairFilter
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter.BottomNavigationReselectedLister
import org.loopring.looprwallet.core.presenters.SearchViewPresenter.OnSearchViewChangeListener
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.homemarkets.adapters.HomeMarketsAdapter
import org.loopring.looprwallet.homemarkets.adapters.OnGeneralMarketsFilterChangeListener
import org.loopring.looprwallet.homemarkets.viewmodels.HomeMarketsViewModel

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

    private var adapter: HomeMarketsAdapter? = null

    private val homeMarketsViewModel by lazy {
        LooprViewModelFactory.get<HomeMarketsViewModel>(activity!!, "markets-$isFavorites")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = provideAdapter(savedInstanceState)
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(view.context)

        setupOfflineFirstStateAndErrorObserver(homeMarketsViewModel, swipeRefreshLayout)
        setMarketsLiveData()
    }

    abstract fun provideAdapter(savedInstanceState: Bundle?): HomeMarketsAdapter

    final override fun onBottomNavigationReselected() {
        recyclerView?.smoothScrollToPosition(0)
    }

    final override fun onQueryTextGainFocus() {
        // NO OP
    }

    final override fun onSearchItemExpanded() {
    }

    final override fun onSearchItemCollapsed() {
        adapter?.clearFilter()
    }

    final override fun onQueryTextChangeListener(searchQuery: String) {
        adapter?.filterData(listOf(), TradingPair::market, "*$searchQuery*")
    }

    final override fun onSortByChange(newSortByFilter: String) {
        homeMarketsViewModel.onSortByChange(newSortByFilter)
    }

    final override fun onDateFilterChange(newDateFilter: String) {
        setMarketsLiveData()
    }

    final override fun getCurrentDateFilter(): String {
        return adapter?.dateFilter ?: throw IllegalArgumentException("Adapter was not initialized!")
    }

    final override fun getCurrentSortByFilter(): String {
        return adapter?.sortBy ?: throw IllegalArgumentException("Adapter was not initialized!")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        adapter?.onSaveInstanceState(outState)
    }

    // MARK - Protected Methods

    /**
     * Sets the [adapter] to to use a new data set, based on the filter criteria that was
     * provided by the [adapter].
     */
    private fun setMarketsLiveData() {
        val adapter = adapter ?: return

        val marketsFilter = TradingPairFilter(isFavorites, adapter.dateFilter)
        homeMarketsViewModel.getHomeMarkets(this, marketsFilter, adapter.sortBy) { data ->
            setupOfflineFirstDataObserverForAdapter(homeMarketsViewModel, adapter, data)
        }
    }

}