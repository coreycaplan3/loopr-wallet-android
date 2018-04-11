package org.loopring.looprwallet.homeorders.fragments

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.fragment_general_orders.*
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.models.loopr.OrderFilter
import org.loopring.looprwallet.core.models.loopr.OrderFilter.Companion.FILTER_OPEN_ALL
import org.loopring.looprwallet.homeorders.R
import org.loopring.looprwallet.homeorders.adapters.GeneralOrderAdapter

/**
 * Created by Corey Caplan on 1/19/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class HomeOpenOrdersFragment : BaseHomeOrdersFragment() {

    override val layoutResource: Int
        get() = R.layout.fragment_general_orders

    override val recyclerView: RecyclerView
        get() = fragmentContainer

    override fun provideAdapter() = GeneralOrderAdapter(FILTER_OPEN_ALL, activity as BaseActivity)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOrderLiveData()
    }

    private fun setupOrderLiveData() {
        val wallet = walletClient.getCurrentWallet()?.credentials?.address ?: return
        val orderFilter = OrderFilter(wallet, null, adapter.currentDateFilter, adapter.currentOrderStatusFilter)
        generalOrderViewModel?.getOrders(orderFilter) {
            generalOrderViewModel?.removeDataObserver(this)
        }
    }

    override fun onQueryTextChangeListener(searchQuery: String) {
        generalOrderViewModel?.getOrders()
    }

}