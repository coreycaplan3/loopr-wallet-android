package org.loopring.looprwallet.home.fragments

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.view.ViewGroup
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.fragments.BaseTabFragment
import org.loopring.looprwallet.core.extensions.inflate
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter.BottomNavigationReselectedLister
import org.loopring.looprwallet.core.presenters.SearchViewPresenter.OnSearchViewChangeListener
import org.loopring.looprwallet.createorder.activities.CreateOrderActivity
import org.loopring.looprwallet.homeorders.R
import org.loopring.looprwallet.homeorders.fragments.GeneralFilledOrdersFragment
import org.loopring.looprwallet.homeorders.fragments.GeneralOpenOrdersFragment

/**
 * Created by Corey on 1/17/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class: To hold the children *Fragment*s' *ViewPager*s and allow them to be swiped
 * between.
 */
class GeneralOrdersParentFragment : BaseTabFragment(), BottomNavigationReselectedLister,
        OnSearchViewChangeListener {

    override val layoutResource: Int
        get() = R.layout.fragment_orders_parent

    override val tabLayoutId: Int
        get() = R.id.ordersTabs

    override fun createAppbarLayout(fragmentView: ViewGroup, savedInstanceState: Bundle?): AppBarLayout? {
        return fragmentView.inflate(R.layout.appbar_orders, false) as? AppBarLayout
    }

    override fun initializeFloatingActionButton(floatingActionButton: FloatingActionButton) {
        floatingActionButton.setImageResource(R.drawable.ic_account_balance_wallet_white_24dp)
        floatingActionButton.setOnClickListener {
            activity?.let { CreateOrderActivity.route(it, null) }
        }
    }

    override fun getAdapterContent(): List<Pair<String, BaseFragment>> {
        return listOf(
                Pair(getString(R.string.open), GeneralOpenOrdersFragment()),
                Pair(getString(R.string.closed), GeneralFilledOrdersFragment())
        )
    }

    override fun onBottomNavigationReselected() {
        logd("Orders reselected")
        val fragment = adapter.getItem(viewPager.currentItem)
        (fragment as BottomNavigationReselectedLister).onBottomNavigationReselected()
    }

    override fun onQueryTextGainFocus() {
        // NO OP
    }

    /*
     * MARK - Search Methods
     *
     * We just forward the events to all of the child fragments
     */

    override fun onQueryTextChangeListener(searchQuery: String) {
        for (i in 0..adapter.count) {
            (adapter.getItem(i) as OnSearchViewChangeListener).onQueryTextChangeListener(searchQuery)
        }
    }

    override fun onSearchItemExpanded() {
        for (i in 0..adapter.count) {
            (adapter.getItem(i) as OnSearchViewChangeListener).onSearchItemExpanded()
        }
    }

    override fun onSearchItemCollapsed() {
        for (i in 0..adapter.count) {
            (adapter.getItem(i) as OnSearchViewChangeListener).onSearchItemCollapsed()
        }
    }
}