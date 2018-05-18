package org.loopring.looprwallet.homeorders.fragments

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.loopr.orders.AppLooprOrder
import org.loopring.looprwallet.core.models.loopr.orders.OrderSummaryFilter
import org.loopring.looprwallet.core.models.loopr.orders.OrderSummaryPager
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter.BottomNavigationReselectedLister
import org.loopring.looprwallet.core.presenters.SearchViewPresenter.OnSearchViewChangeListener
import org.loopring.looprwallet.core.presenters.SearchViewPresenter.SearchFragment
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.homeorders.adapters.HomeOrderAdapter
import org.loopring.looprwallet.homeorders.adapters.OnHomeOrderFilterChangeListener
import org.loopring.looprwallet.homeorders.viewmodels.HomeOrdersViewModel

/**
 * Created by Corey Caplan on 4/7/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To provide *base* behavior for the **open** and **closed** orders fragments.
 */
abstract class BaseHomeChildOrdersFragment : BaseFragment(), BottomNavigationReselectedLister,
        OnSearchViewChangeListener, OnHomeOrderFilterChangeListener, OnRefreshListener {

    companion object {

        private const val KEY_ORDER_FILTER = "ORDER_FILTER"

    }

    abstract val swipeRefreshLayout: SwipeRefreshLayout?
    abstract val recyclerView: RecyclerView?

    private var adapter: HomeOrderAdapter? = null

    private val homeOrdersViewModel: HomeOrdersViewModel by lazy {
        LooprViewModelFactory.get<HomeOrdersViewModel>(this)
    }

    override val currentStatusFilter: String
        get() = adapter?.currentStatusFilter ?: throw IllegalStateException("Adapter is null!")

    private lateinit var orderFilter: OrderSummaryFilter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val address = walletClient.getCurrentWallet()?.credentials?.address

        val defaultFilter = OrderSummaryFilter(address, null, orderType, 1)
        orderFilter = savedInstanceState?.getParcelable(KEY_ORDER_FILTER) ?: defaultFilter
        if (address != null) {
            adapter = provideAdapter(orderFilter).apply {
                this.onLoadMore = ::onLoadMoreHomeOrders
            }
        }

        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(view.context)

        setupOfflineFirstStateAndErrorObserver(homeOrdersViewModel, swipeRefreshLayout)
        setOrderLiveData()
    }

    abstract val orderType: String

    abstract fun provideAdapter(orderFilter: OrderSummaryFilter): HomeOrderAdapter

    override fun onRefresh() {
        homeOrdersViewModel.refresh()
    }

    final override fun onBottomNavigationReselected() {
        recyclerView?.smoothScrollToPosition(0)
    }

    final override fun onQueryTextGainFocus() {
        // NO OP
    }

    final override fun onSearchItemExpanded() {
        // NO OP
    }

    final override fun onSearchItemCollapsed() {
        adapter?.clearFilter()
    }

    final override fun onQueryTextChangeListener(searchQuery: String) {
        adapter?.filterData(listOf(AppLooprOrder::tradingPair), TradingPair::market, "*$searchQuery*")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(KEY_ORDER_FILTER, orderFilter)
    }

    // MARK - Private Methods

    private fun onLoadMoreHomeOrders() {
        orderFilter.pageNumber += 1
        homeOrdersViewModel.refresh()
    }

    /**
     * Resets the [adapter] to its initial set of data, based on the filter criteria that was
     * provided by the [adapter].
     */
    private fun setOrderLiveData() {
        val adapter = adapter ?: return

        homeOrdersViewModel.getOrders(this, orderFilter) { orderContainer ->

            setupOfflineFirstDataObserverForAdapter(homeOrdersViewModel, adapter, orderContainer.data)
            (adapter.pager as? OrderSummaryPager)?.orderContainer = orderContainer

            val presenter = (parentFragment as? SearchFragment)?.searchViewPresenter
            val searchQuery = presenter?.searchQuery
            if (presenter?.isExpanded == true && searchQuery != null) {
                // We're currently searching
                onQueryTextChangeListener(searchQuery)
            }
        }
    }

}