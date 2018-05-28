package org.loopring.looprwallet.homeorders.fragments

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import org.loopring.looprwallet.core.extensions.findViewById
import org.loopring.looprwallet.core.models.loopr.orders.OrderSummaryFilter
import org.loopring.looprwallet.homeorders.R

/**
 * Created by Corey on 4/11/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A fragment in a set of tabs for displaying the user's cancelled orders
 */
class HomeCancelledOrdersFragment : BaseHomeChildOrdersFragment() {

    override val layoutResource: Int
        get() = R.layout.fragment_home_orders

    override val swipeRefreshLayout: SwipeRefreshLayout?
        get() = findViewById(R.id.fragmentContainerSwipeRefresh)

    override val recyclerView: RecyclerView?
        get() = findViewById(R.id.fragmentContainer)

    override val orderType: String
        get() = OrderSummaryFilter.FILTER_CANCELLED

}