package org.loopring.looprwallet.homeorders.fragments

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import io.realm.RealmList
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrder
import org.loopring.looprwallet.core.models.loopr.orders.OrderLooprPager
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

    abstract val swipeRefreshLayout: SwipeRefreshLayout?
    abstract val recyclerView: RecyclerView?

    private var adapter: HomeOrderAdapter? = null

    private val homeOrdersViewModel: HomeOrdersViewModel by lazy {
        LooprViewModelFactory.get<HomeOrdersViewModel>(this)
    }

    override val currentStatusFilter: String
        get() = adapter?.currentStatusFilter ?: throw IllegalStateException("Adapter is null!")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val address = walletClient.getCurrentWallet()?.credentials?.address

        if (address != null) {
            adapter = provideAdapter(savedInstanceState, address).apply {
                this.onLoadMore = ::onLoadMoreHomeOrders
            }
        }

        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(view.context)

        setupOfflineFirstStateAndErrorObserver(homeOrdersViewModel, swipeRefreshLayout)
        setOrderLiveData(1)
    }

    abstract fun provideAdapter(savedInstanceState: Bundle?, address: String): HomeOrderAdapter

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
        adapter?.let {
            it.updateData(it.pager.data)
        }
    }

    final override fun onQueryTextChangeListener(searchQuery: String) {
        val adapter = adapter ?: return
        async(UI) {
            val filteredList: List<LooprOrder>? = adapter.data?.filter {
                it.tradingPair.market.contains("*$searchQuery*", ignoreCase = true)
            }

            if (filteredList != null) {
                val realmList = RealmList<LooprOrder>().apply {
                    addAll(filteredList)
                }

                adapter.updateData(realmList)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        adapter?.onSaveInstanceState(outState)
    }

    // MARK - Private Methods

    private fun onLoadMoreHomeOrders() {
        adapter?.orderFilter?.pageNumber?.let { pageNumber ->
            setOrderLiveData(pageNumber + 1)
        }
    }

    /**
     * Resets the [adapter] to its initial set of data, based on the filter criteria that was
     * provided by the [adapter].
     */
    private fun setOrderLiveData(pageNumber: Int) {
        val adapter = adapter ?: return

        adapter.orderFilter.pageNumber = pageNumber
        homeOrdersViewModel.getOrders(this, adapter.orderFilter) { orderContainer ->
            setupOfflineFirstDataObserverForAdapter(homeOrdersViewModel, adapter, orderContainer.data)
            (adapter.pager as? OrderLooprPager)?.orderContainer = orderContainer

            val presenter = (parentFragment as? SearchFragment)?.searchViewPresenter
            val searchQuery = presenter?.searchQuery
            if (presenter?.isExpanded == true && searchQuery != null) {
                // We're currently searching
                onQueryTextChangeListener(searchQuery)
            }
        }
    }

}