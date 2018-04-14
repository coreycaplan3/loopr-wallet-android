package org.loopring.looprwallet.homemarkets.fragments

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuInflater
import android.view.ViewGroup
import org.loopring.looprwallet.core.extensions.inflate
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.fragments.BaseTabFragment
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter.BottomNavigationReselectedLister
import org.loopring.looprwallet.core.presenters.SearchViewPresenter
import org.loopring.looprwallet.core.presenters.SearchViewPresenter.OnSearchViewChangeListener
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.homemarkets.R

/**
 * Created by Corey on 1/17/2018.
 *
 *  Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class HomeMarketsParentFragment : BaseTabFragment(), BottomNavigationReselectedLister,
        OnSearchViewChangeListener {

    override val layoutResource: Int
        get() = R.layout.fragment_markets_parent

    override val tabLayoutId: Int
        get() = R.id.marketsTabs

    private lateinit var searchViewPresenter: SearchViewPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        searchViewPresenter = SearchViewPresenter(
                containsOverflowMenu = true,
                numberOfVisibleMenuItems = 1,
                baseFragment = this,
                savedInstanceState = savedInstanceState,
                listener = this
        )
    }

    override fun createAppbarLayout(fragmentView: ViewGroup, savedInstanceState: Bundle?): AppBarLayout? {
        return fragmentView.inflate(R.layout.appbar_markets, false) as AppBarLayout?
    }

    override fun getAdapterContent(): List<Pair<String, BaseFragment>> {
        return listOf(
                Pair(str(R.string.all), HomeAllMarketsFragment()),
                Pair(str(R.string.favorites), HomeFavoriteMarketsFragment())
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(org.loopring.looprwallet.core.R.menu.menu_main_search, menu)

        val searchItem = menu.findItem(R.id.menu_main_search)
        val searchView = searchItem.actionView as SearchView

        searchViewPresenter.setupSearchView(searchItem, searchView)
    }

    override fun onBottomNavigationReselected() {
        logd("Markets reselected")
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        searchViewPresenter.onSaveInstanceState(outState)
    }

}