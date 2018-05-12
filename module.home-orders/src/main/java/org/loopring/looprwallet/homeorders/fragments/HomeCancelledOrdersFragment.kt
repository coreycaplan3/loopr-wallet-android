package org.loopring.looprwallet.homeorders.fragments

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.extensions.findViewById
import org.loopring.looprwallet.core.models.loopr.orders.OrderFilter
import org.loopring.looprwallet.homeorders.R
import org.loopring.looprwallet.homeorders.adapters.HomeOrderAdapter

/**
 * Created by Corey on 4/11/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A fragment in a set of tabs for displaying the user's cancelled orders
 */
class HomeCancelledOrdersFragment : BaseHomeChildOrdersFragment() {

    override val layoutResource: Int
        get() = R.layout.fragment_general_orders

    override val swipeRefreshLayout: SwipeRefreshLayout?
        get() = findViewById(R.id.fragmentContainerSwipeRefresh)

    override val recyclerView: RecyclerView?
        get() = findViewById(R.id.fragmentContainer)

    override fun provideAdapter(savedInstanceState: Bundle?, address: String): HomeOrderAdapter {
        val activity = activity as? BaseActivity
                ?: throw IllegalStateException("Activity cannot be cast!")
        return HomeOrderAdapter(savedInstanceState, address, OrderFilter.FILTER_CANCELLED, activity, this)
    }

}