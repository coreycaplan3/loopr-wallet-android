package org.loopring.looprwallet.homeorders.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.order.OrderFilter
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter.BottomNavigationReselectedLister
import org.loopring.looprwallet.core.presenters.SearchViewPresenter.OnSearchViewChangeListener
import org.loopring.looprwallet.core.viewmodels.LooprWalletViewModelFactory
import org.loopring.looprwallet.homeorders.adapters.GeneralOrderAdapter
import org.loopring.looprwallet.homeorders.adapters.OnGeneralOrderFilterChangeListener
import org.loopring.looprwallet.homeorders.viewmodels.GeneralOrderViewModel

/**
 * Created by Corey Caplan on 4/7/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To provide *base* behavior for the **open** and **closed** orders fragments.
 */
abstract class BaseHomeOrdersFragment : BaseFragment(), BottomNavigationReselectedLister,
        OnSearchViewChangeListener, OnGeneralOrderFilterChangeListener {

    companion object {
        private const val KEY_IS_SEARCH_ACTIVE = "_IS_SEARCH_ACTIVE"
    }

    abstract val recyclerView: RecyclerView

    var isSearchActive = false
        private set

    lateinit var adapter: GeneralOrderAdapter
        private set

    var generalOrderViewModel: GeneralOrderViewModel? = null
        get() {
            if (field != null) {
                return field
            }

            val wallet = walletClient.getCurrentWallet() ?: return null

            field = LooprWalletViewModelFactory.get(this, wallet)
            return field
        }
        private set

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

    abstract fun provideAdapter(savedInstanceState: Bundle?): GeneralOrderAdapter

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

        outState.putBoolean(KEY_IS_SEARCH_ACTIVE, isSearchActive)
        adapter.onSaveInstanceState(outState)
    }

    // MARK - Protected Methods

    /**
     * Resets the [adapter] to its initial set of data, based on the filter criteria that was
     * provided by the [adapter].
     */
    protected fun resetOrderLiveData() {
        setOrderLiveData()
    }

    // MARK - Private Methods

    private fun setOrderLiveData(ticker: String? = null) {
        val address = walletClient.getCurrentWallet()?.credentials?.address ?: return
        val orderFilter = OrderFilter(address, ticker, adapter.currentDateFilter, adapter.currentOrderStatusFilter)

        generalOrderViewModel?.getOrders(this, orderFilter) {
            adapter.updateData(it)
            generalOrderViewModel?.removeDataObserver(this)
        }
    }

}