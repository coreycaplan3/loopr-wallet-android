package com.caplaninnovations.looprwallet.fragments.orders

import android.os.Bundle
import android.support.design.widget.TabLayout
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

    override var tabLayout: TabLayout? = null
        get() = activity?.findViewById(R.id.ordersTabs)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_general_with_view_pager, container, false)
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