package org.loopring.looprwallet.homeorders.fragments

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.loopr.orders.OrderSummaryFilter
import org.loopring.looprwallet.core.models.loopr.orders.OrderSummaryPager
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter.BottomNavigationReselectedLister
import org.loopring.looprwallet.core.presenters.SearchViewPresenter.OnSearchViewChangeListener
import org.loopring.looprwallet.core.presenters.SearchViewPresenter.SearchFragment
import org.loopring.looprwallet.core.repositories.loopr.LooprOrderRepository
import org.loopring.looprwallet.core.utilities.RealmUtility
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
        OnSearchViewChangeListener, OnHomeOrderFilterChangeListener {

    companion object {

        private const val KEY_ORDER_FILTER = "ORDER_FILTER"

    }

    abstract val swipeRefreshLayout: SwipeRefreshLayout?
    abstract val recyclerView: RecyclerView?

    val adapter: HomeOrderAdapter by lazy {
        HomeOrderAdapter(orderType, activity as BaseActivity, this)
    }

    private val homeOrdersViewModel: HomeOrdersViewModel by lazy {
        LooprViewModelFactory.get<HomeOrdersViewModel>(activity!!, "orders-$orderType")
    }

    override val currentStatusFilter: String
        get() = adapter.currentStatusFilter

    private lateinit var orderFilter: OrderSummaryFilter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val address = walletClient.getCurrentWallet()?.credentials?.address

        val defaultFilter = OrderSummaryFilter(address, null, orderType, 1)
        orderFilter = savedInstanceState?.getParcelable(KEY_ORDER_FILTER) ?: defaultFilter

        adapter.onLoadMore = ::onLoadMoreHomeOrders
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(view.context)

        setupOfflineFirstStateAndErrorObserver(homeOrdersViewModel, swipeRefreshLayout)
        setupOrderLiveData()
    }

    abstract val orderType: String

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
        adapter.clearFilter()
    }

    final override fun onQueryTextChangeListener(searchQuery: String) {
        val repository = LooprOrderRepository()
        adapter.data?.let {
            adapter.filterData(repository.filterOrdersByQuery(searchQuery, it))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(KEY_ORDER_FILTER, orderFilter)
    }

    // MARK - Private Methods

    private fun onLoadMoreHomeOrders() {
        RealmUtility.loadMorePaging(adapter.pager, orderFilter, homeOrdersViewModel)
    }

    private fun setupOrderLiveData() {
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