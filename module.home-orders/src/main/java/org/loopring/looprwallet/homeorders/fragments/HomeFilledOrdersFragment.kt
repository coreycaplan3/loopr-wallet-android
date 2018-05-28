package org.loopring.looprwallet.homeorders.fragments

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.extensions.findViewById
import org.loopring.looprwallet.core.models.loopr.orders.OrderSummaryFilter
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
        get() = R.layout.fragment_home_orders

    override val swipeRefreshLayout: SwipeRefreshLayout?
        get() = findViewById(R.id.fragmentContainerSwipeRefresh)

    override val recyclerView: RecyclerView?
        get() = findViewById(R.id.fragmentContainer)

    override val orderType: String
        get() = FILTER_FILLED

}