package com.caplaninnovations.looprwallet.fragments.orders

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.fragments.BaseTabFragment
import com.caplaninnovations.looprwallet.handlers.BottomNavigationHandler
import com.caplaninnovations.looprwallet.utilities.animateScaleBoth
import com.caplaninnovations.looprwallet.utilities.animateScaleBothAndStart
import com.caplaninnovations.looprwallet.utilities.inflate
import com.caplaninnovations.looprwallet.utilities.logd

/**
 * Created by Corey on 1/17/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class OrdersParentFragment : BaseTabFragment(), BottomNavigationHandler.BottomNavigationReselectedLister {

    override val layoutResource: Int
        get() = R.layout.fragment_orders_parent

    override val tabLayoutId: Int
        get() = R.id.ordersTabs

    override fun createAppbarLayout(fragmentView: ViewGroup, savedInstanceState: Bundle?): AppBarLayout? {
        return fragmentView.inflate(R.layout.appbar_orders, false) as AppBarLayout?
    }

    override fun initializeFloatingActionButton(floatingActionButton: FloatingActionButton) {
        floatingActionButton.setImageResource(R.drawable.ic_account_balance_wallet_white_24dp)
        floatingActionButton.setOnClickListener { logd("FAB CLICKED!") }
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