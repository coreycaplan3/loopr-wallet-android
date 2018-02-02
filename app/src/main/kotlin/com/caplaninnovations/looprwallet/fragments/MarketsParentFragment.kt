package com.caplaninnovations.looprwallet.fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.android.fragments.LooprFragmentPagerAdapter
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationHandler
import com.caplaninnovations.looprwallet.utilities.*
import kotlinx.android.synthetic.main.fragment_markets_parent.*

/**
 * Created by Corey on 1/17/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
class MarketsParentFragment : BaseTabFragment(), BottomNavigationHandler.OnBottomNavigationReselectedLister {

    override var tabLayout: TabLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tabLayout = activity?.findViewById(R.id.marketsTabs)
        return inflater.inflate(R.layout.fragment_markets_parent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        marketsViewPager.adapter = LooprFragmentPagerAdapter(childFragmentManager, listOf(
                Pair(getString(R.string.all), AllMarketsFragment()),
                Pair(getString(R.string.favorites), FavoriteMarketsFragment())
        ))

        tabLayout?.setupWithViewPager(marketsViewPager)
    }

    override fun onBottomNavigationReselected() {
        logd("Markets reselected")
    }

}