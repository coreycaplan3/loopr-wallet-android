package com.caplaninnovations.looprwallet.fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationHandler
import com.caplaninnovations.looprwallet.utilities.logd
import kotlinx.android.synthetic.main.fragment_orders_parent.*

/**
 * Created by Corey on 1/17/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
class OrdersParentFragment : BaseTabFragment(), BottomNavigationHandler.OnBottomNavigationReselectedLister {

    override var tabLayout: TabLayout? = null
        get() = activity?.findViewById(R.id.ordersTabs)

    override var viewPager: ViewPager? = null
        get() = ordersViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_orders_parent, container, false)
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