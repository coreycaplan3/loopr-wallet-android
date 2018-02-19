package com.caplaninnovations.looprwallet.fragments.markets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.fragments.BaseTabFragment
import com.caplaninnovations.looprwallet.handlers.BottomNavigationHandler
import com.caplaninnovations.looprwallet.utilities.*

/**
 * Created by Corey on 1/17/2018.
 *
 *  Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class MarketsParentFragment : BaseTabFragment(), BottomNavigationHandler.OnBottomNavigationReselectedLister {

    override val tabLayoutName: String
        get() = "markets-tab"

    override val layoutResource: Int
        get() = R.layout.fragment_markets_parent

    override val tabLayoutId: Int
        get() = R.id.fragmentMarketsParentTabLayout

    override fun createAppbarLayout(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.appbar_markets, container, false)
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