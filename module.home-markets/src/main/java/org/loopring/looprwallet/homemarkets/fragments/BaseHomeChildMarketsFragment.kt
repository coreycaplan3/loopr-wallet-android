package org.loopring.looprwallet.homemarkets.fragments

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.loopring.looprwallet.core.extensions.ifNotNull
import org.loopring.looprwallet.core.fragments.BaseFragment
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
        LooprViewModelFactory.get<HomeMarketsViewModel>(this)
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
    }

    final override fun onQueryTextChangeListener(searchQuery: String) {
        setMarketsLiveData(searchQuery)
    }

    final override fun onSortByChange(newSortByFilter: String) {
        setMarketsLiveData()
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
    private fun setMarketsLiveData(ticker: String? = null) {
        adapter?.ifNotNull { adapter ->

            val marketsFilter = TradingPairFilter(ticker, isFavorites, adapter.dateFilter, adapter.sortBy)
            homeMarketsViewModel.getHomeMarkets(this, marketsFilter) { data ->
                setupOfflineFirstDataObserverForAdapter(homeMarketsViewModel, adapter, data)
            }
        }
    }

}