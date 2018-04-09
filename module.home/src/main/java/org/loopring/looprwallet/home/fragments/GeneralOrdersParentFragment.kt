package org.loopring.looprwallet.home.fragments

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.view.ViewGroup
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.fragments.BaseTabFragment
import org.loopring.looprwallet.core.extensions.inflate
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter
import org.loopring.looprwallet.homeorders.R
import org.loopring.looprwallet.homeorders.fragments.GeneralClosedOrdersFragment
import org.loopring.looprwallet.homeorders.fragments.GeneralOpenOrdersFragment

/**
 * Created by Corey on 1/17/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class GeneralOrdersParentFragment : BaseTabFragment(), BottomNavigationPresenter.BottomNavigationReselectedLister {

    override val layoutResource: Int
        get() = R.layout.fragment_orders_parent

    override val tabLayoutId: Int
        get() = R.id.ordersTabs

    override fun createAppbarLayout(fragmentView: ViewGroup, savedInstanceState: Bundle?): AppBarLayout? {
        return fragmentView.inflate(R.layout.appbar_orders, false) as AppBarLayout?
    }

    override fun initializeFloatingActionButton(floatingActionButton: FloatingActionButton) {
        floatingActionButton.setImageResource(R.drawable.ic_account_balance_wallet_white_24dp)
        floatingActionButton.setOnClickListener {
            logd("FAB CLICKED!")
        }
    }

    override fun getAdapterContent(): List<Pair<String, BaseFragment>> {
        return listOf(
                Pair(getString(R.string.open), GeneralOpenOrdersFragment()),
                Pair(getString(R.string.closed), GeneralClosedOrdersFragment())
        )
    }

    override fun onBottomNavigationReselected() {
        logd("Orders reselected")
    }

}