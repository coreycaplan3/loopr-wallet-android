package org.loopring.looprwallet.homeorders.fragments

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.extensions.findViewById
import org.loopring.looprwallet.core.models.loopr.orders.OrderSummaryFilter.Companion.FILTER_FILLED
import org.loopring.looprwallet.homeorders.R
import org.loopring.looprwallet.homeorders.adapters.HomeOrderAdapter

/**
 * Created by Corey Caplan on 1/19/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class HomeFilledOrdersFragment : BaseHomeChildOrdersFragment() {

    override val layoutResource: Int
        get() = R.layout.fragment_general_orders

    override val swipeRefreshLayout: SwipeRefreshLayout?
        get() = findViewById(R.id.fragmentContainerSwipeRefresh)

    override val recyclerView: RecyclerView?
        get() = findViewById(R.id.fragmentContainer)

    override fun provideAdapter(savedInstanceState: Bundle?, address: String): HomeOrderAdapter {
        val activity = activity as? BaseActivity
                ?: throw IllegalStateException("Cannot cast activity")
        return HomeOrderAdapter(savedInstanceState, address, FILTER_FILLED, activity, this)
    }

}