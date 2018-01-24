package com.caplaninnovations.looprwallet.fragments

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BottomNavigationActivity
import com.caplaninnovations.looprwallet.utilities.logd
import com.caplaninnovations.looprwallet.utilities.str
import kotlinx.android.synthetic.main.fragment_markets_parent.*
import kotlinx.android.synthetic.main.fragment_orders_parent.*

/**
 * Created by Corey on 1/17/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
class OrdersParentFragment : BaseFragment(), BottomNavigationActivity.OnBottomNavigationReselectedLister {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_orders_parent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ordersViewPager.adapter = OrdersFragmentStatePagerAdapter(childFragmentManager)
        ordersTabs.setupWithViewPager(ordersViewPager)
    }

    override fun onBottomNavigationReselected() {
        logd("Orders reselected")
    }

    class OrdersFragmentStatePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

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

        override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
            // TODO
        }

        override fun saveState(): Parcelable? {
            // TODO
            return null
        }
    }

}