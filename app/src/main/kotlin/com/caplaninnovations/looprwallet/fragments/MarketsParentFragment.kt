package com.caplaninnovations.looprwallet.fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationHandler
import com.caplaninnovations.looprwallet.utilities.*
import kotlinx.android.synthetic.main.fragment_markets_parent.*

/**
 * Created by Corey on 1/17/2018.
 *
 *  Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class MarketsParentFragment : BaseTabFragment(), BottomNavigationHandler.OnBottomNavigationReselectedLister {

    override var tabLayout: TabLayout? = null
        get() = activity?.findViewById(R.id.marketsTabs)

    override var viewPager: ViewPager? = null
        get() = marketsViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_markets_parent, container, false)
    }

    override fun getAdapterContent(): List<Pair<String, BaseFragment>> {
        return listOf(
                Pair(getString(R.string.all), AllMarketsFragment()),
                Pair(getString(R.string.favorites), FavoriteMarketsFragment())
        )
    }

    override fun onBottomNavigationReselected() {
        logd("Markets reselected")
    }

}