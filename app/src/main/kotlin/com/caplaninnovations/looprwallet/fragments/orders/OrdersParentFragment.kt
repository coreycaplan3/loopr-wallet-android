package com.caplaninnovations.looprwallet.fragments.orders

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.fragments.BaseTabFragment
import com.caplaninnovations.looprwallet.handlers.BottomNavigationHandler
import com.caplaninnovations.looprwallet.utilities.logd

/**
 * Created by Corey on 1/17/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class OrdersParentFragment : BaseTabFragment(), BottomNavigationHandler.OnBottomNavigationReselectedLister {

    override val tabLayoutTransitionName: String
        get() = "orders-tab"

    override val layoutResource: Int
        get() = R.layout.fragment_orders_parent

    override val tabLayoutId: Int
        get() = R.id.ordersTabs

    override fun createAppbarLayout(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): AppBarLayout? {
        return inflater.inflate(R.layout.appbar_orders, container, false) as AppBarLayout?
    }

    override fun getAdapterContent(): List<Pair<String, BaseFragment>> {
        return listOf(
                Pair(getString(R.string.open), OpenOrdersFragment()),
                Pair(getString(R.string.closed), ClosedOrdersFragment())
        )
    }

    override fun onBottomNavigationReselected() {
        logd("Orders reselected")
    }

}