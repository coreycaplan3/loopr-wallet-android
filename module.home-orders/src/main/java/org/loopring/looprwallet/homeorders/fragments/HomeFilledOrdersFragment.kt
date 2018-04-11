package org.loopring.looprwallet.homeorders.fragments

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.fragment_general_orders.*
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.models.loopr.OrderFilter
import org.loopring.looprwallet.core.models.loopr.OrderFilter.Companion.FILTER_FILLED
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
class HomeFilledOrdersFragment : BaseHomeOrdersFragment() {

    override val layoutResource: Int
        get() = R.layout.fragment_general_orders

    override val recyclerView: RecyclerView
        get() = fragmentContainer

    override fun provideAdapter() = GeneralOrderAdapter(FILTER_FILLED, activity as BaseActivity)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val address = walletClient.getCurrentWallet()?.credentials?.address
        address?.let {
            val orderFilter = OrderFilter(it, adapter.currentDateFilter, adapter.currentOrderStatusFilter)
            generalOrderViewModel?.getFilledOrders(orderFilter)
        }
    }

    override fun onQueryTextChangeListener(searchQuery: String) {
        TODO("not implemented")
    }

    fun onFilterChanged

}