package org.loopring.looprwallet.homemarkets.fragments

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.view.ViewGroup
import org.loopring.looprwallet.home.R
import org.loopring.looprwallet.core.extensions.inflate
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.fragments.BaseTabFragment
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter
import org.loopring.looprwallet.homemarkets.fragments.AllMarketsFragment
import org.loopring.looprwallet.homemarkets.fragments.FavoriteMarketsFragment

/**
 * Created by Corey on 1/17/2018.
 *
 *  Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class MarketsParentFragment : BaseTabFragment(), BottomNavigationPresenter.BottomNavigationReselectedLister {

    override val layoutResource: Int
        get() = R.layout.fragment_markets_parent

    override val tabLayoutId: Int
        get() = R.id.marketsTabs

    override fun createAppbarLayout(fragmentView: ViewGroup, savedInstanceState: Bundle?): AppBarLayout? {
        return fragmentView.inflate(R.layout.appbar_markets, false) as AppBarLayout?
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