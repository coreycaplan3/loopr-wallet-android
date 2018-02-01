package com.caplaninnovations.looprwallet.fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.android.fragments.LooprFragmentPagerAdapter
import com.caplaninnovations.looprwallet.utilities.logd
import kotlinx.android.synthetic.main.fragment_orders_parent.*

/**
 * Created by Corey on 1/17/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
class OrdersParentFragment : BaseTabFragment(), BottomNavigationActivity.OnBottomNavigationReselectedLister {

    override var tabLayout: TabLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tabLayout = activity?.findViewById(R.id.ordersTabs)
        return inflater.inflate(R.layout.fragment_orders_parent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ordersViewPager.adapter = LooprFragmentPagerAdapter(childFragmentManager, listOf(
                Pair(getString(R.string.open), OpenOrdersFragment()),
                Pair(getString(R.string.closed), ClosedOrdersFragment())
        ))

        tabLayout?.setupWithViewPager(ordersViewPager)
    }

    override fun onBottomNavigationReselected() {
        logd("Orders reselected")
    }

}