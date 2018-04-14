package org.loopring.looprwallet.homemarkets.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.markets.MarketsFilter
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter
import org.loopring.looprwallet.core.presenters.SearchViewPresenter
import org.loopring.looprwallet.core.viewmodels.LooprWalletViewModelFactory
import org.loopring.looprwallet.homemarkets.adapters.HomeMarketsAdapter
import org.loopring.looprwallet.homemarkets.adapters.OnGeneralMarketsFilterChangeListener
import org.loopring.looprwallet.homemarkets.viewmodels.HomeMarketsViewModel

/**
 * Created by Corey Caplan on 4/13/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A class that enables the sharing of functionality across [AllMarketsFragment]
 * and [FavoriteMarketsFragment].
 *
 */
abstract class BaseHomeMarketsFragment : BaseFragment(), BottomNavigationPresenter.BottomNavigationReselectedLister,
        SearchViewPresenter.OnSearchViewChangeListener, OnGeneralMarketsFilterChangeListener {

    companion object {
        private const val KEY_IS_SEARCH_ACTIVE = "_IS_SEARCH_ACTIVE"
    }

    abstract val recyclerView: RecyclerView

    /**
     * True if this fragment is showing favorites or false otherwise
     */
    abstract val isFavorites: Boolean

    var isSearchActive = false
        private set

    lateinit var adapter: HomeMarketsAdapter
        private set

    val homeMarketsViewModel by lazy {
        LooprWalletViewModelFactory.get<HomeMarketsViewModel>(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isSearchActive = savedInstanceState?.getBoolean(KEY_IS_SEARCH_ACTIVE, false) ?: false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = provideAdapter(savedInstanceState)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)
    }

    abstract fun provideAdapter(savedInstanceState: Bundle?): HomeMarketsAdapter

    final override fun onBottomNavigationReselected() {
        recyclerView.smoothScrollToPosition(0)
    }

    final override fun onQueryTextGainFocus() {
        // NO OP
    }

    final override fun onSearchItemExpanded() {
        isSearchActive = true
    }

    final override fun onSearchItemCollapsed() {
        isSearchActive = false
    }

    final override fun onQueryTextChangeListener(searchQuery: String) {
        setMarketsLiveData(searchQuery)
    }

    final override fun onSortByChange(sortByValue: String) {
        setMarketsLiveData()
    }

    final override fun onDateFilterChange(dateValue: String) {
        setMarketsLiveData()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(KEY_IS_SEARCH_ACTIVE, isSearchActive)
        adapter.onSaveInstanceState(outState)
    }

    // MARK - Protected Methods

    /**
     * Resets the [adapter] to its initial set of data, based on the filter criteria that was
     * provided by the [adapter].
     */
    protected fun resetMarketLiveData() {
        setMarketsLiveData()
    }

    // MARK - Private Methods

    private fun setMarketsLiveData(ticker: String? = null) {
        val marketsFilter = MarketsFilter(ticker, isFavorites, adapter.dateFilter, adapter.sortBy)

        homeMarketsViewModel.getHomeMarkets(this, marketsFilter) {
            adapter.updateData(it)
            homeMarketsViewModel.removeDataObserver(this)
        }
    }

}