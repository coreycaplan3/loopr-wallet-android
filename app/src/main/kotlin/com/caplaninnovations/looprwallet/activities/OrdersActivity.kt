package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.fragments.ClosedOrdersFragment
import com.caplaninnovations.looprwallet.fragments.OpenOrdersFragment
import com.caplaninnovations.looprwallet.utilities.str
import kotlinx.android.synthetic.main.activity_orders.*

class OrdersActivity : BottomNavigationActivity() {

    override val navigationContainer: Int
        get() = R.layout.activity_orders

    override val selectedTab: String
        get() = tagOrders

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ordersViewPager.adapter = OrdersFragmentStatePagerAdapter(supportFragmentManager)
        ordersTabs.setupWithViewPager(ordersViewPager)
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        // TODO
    }

    private class OrdersFragmentStatePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> OpenOrdersFragment()
                1 -> ClosedOrdersFragment()
                else -> throw IllegalArgumentException("Invalid argument, found $position")
            }
        }

        override fun getCount(): Int = 2

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> str(R.string.open)
                1 -> str(R.string.closed)
                else -> throw IllegalArgumentException("Invalid argument, found $position")
            }
        }

    }

}
