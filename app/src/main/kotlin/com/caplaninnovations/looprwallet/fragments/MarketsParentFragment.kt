package com.caplaninnovations.looprwallet.fragments

import android.os.Bundle
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

/**
 * Created by Corey on 1/17/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
class MarketsParentFragment : BaseFragment(), BottomNavigationActivity.OnBottomNavigationReselectedLister {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return  inflater.inflate(R.layout.fragment_markets_parent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        marketsViewPager.adapter = MarketsFragmentStatePagerAdapter(childFragmentManager)
        marketsTabs.setupWithViewPager(marketsViewPager)
    }

    override fun onBottomNavigationReselected() {
        logd("Markets reselected")
    }

    class MarketsFragmentStatePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> AllMarketsFragment()
                1 -> FavoriteMarketsFragment()
                else -> throw IllegalArgumentException("Invalid argument, found $position")
            }
        }

        override fun getCount(): Int = 2

        override fun getPageTitle(position: Int): CharSequence? {
            return when(position) {
                0 -> str(R.string.all)
                1 -> str(R.string.favorites)
                else -> throw IllegalArgumentException("Invalid argument, found $position")
            }
        }

    }

}