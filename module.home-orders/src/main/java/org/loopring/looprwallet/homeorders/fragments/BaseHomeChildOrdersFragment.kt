package org.loopring.looprwallet.homeorders.fragments

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.order.OrderFilter
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter.BottomNavigationReselectedLister
import org.loopring.looprwallet.core.presenters.SearchViewPresenter.OnSearchViewChangeListener
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.homeorders.adapters.GeneralOrderAdapter
import org.loopring.looprwallet.homeorders.adapters.OnGeneralOrderFilterChangeListener
import org.loopring.looprwallet.homeorders.viewmodels.GeneralOrdersViewModel

/**
 * Created by Corey Caplan on 4/7/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To provide *base* behavior for the **open** and **closed** orders fragments.
 */
abstract class BaseHomeChildOrdersFragment : BaseFragment(), BottomNavigationReselectedLister,
        OnSearchViewChangeListener, OnGeneralOrderFilterChangeListener, OnRefreshListener {

    abstract val swipeRefreshLayout: SwipeRefreshLayout
    abstract val recyclerView: RecyclerView

    var isSearchActive = false
        private set

    lateinit var adapter: GeneralOrderAdapter
        private set

    private var generalOrdersViewModel: GeneralOrdersViewModel? = null
        get() {
            if (field != null) {
                return field
            }

            val wallet = walletClient.getCurrentWallet() ?: return null

            field = LooprViewModelFactory.get(this, wallet)
            return field
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = provideAdapter(savedInstanceState)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        swipeRefreshLayout.setOnRefreshListener(this)

        setOrderLiveData(null)
    }

    abstract fun provideAdapter(savedInstanceState: Bundle?): GeneralOrderAdapter

    override fun onRefresh() {
        generalOrdersViewModel?.refresh()
    }

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
        setOrderLiveData(searchQuery)
    }

    final override fun onStatusFilterChange(newStatusValue: String) {
        setOrderLiveData()
    }

    final override fun onDateFilterChange(newDateValue: String) {
        setOrderLiveData()
    }

    override fun getCurrentDateFilterChange(): String {
        return adapter.currentDateFilter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        adapter.onSaveInstanceState(outState)
    }

    // MARK - Private Methods

    /**
     * Resets the [adapter] to its initial set of data, based on the filter criteria that was
     * provided by the [adapter].
     */
    private fun setOrderLiveData(ticker: String? = null) {
        val address = walletClient.getCurrentWallet()?.credentials?.address ?: return
        val orderFilter = OrderFilter(address, ticker, adapter.currentDateFilter, adapter.currentOrderStatusFilter)

        generalOrdersViewModel?.getOrders(this, orderFilter) {
            adapter.updateData(it)
            generalOrdersViewModel?.removeDataObserver(this)
        }
    }

}