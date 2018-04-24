package org.loopring.looprwallet.homeorders.fragments

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import org.loopring.looprwallet.barcode.activities.BarcodeCaptureActivity
import org.loopring.looprwallet.core.activities.SettingsActivity
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.fragments.BaseTabFragment
import org.loopring.looprwallet.core.extensions.inflate
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.models.markets.TradingPair
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter.BottomNavigationReselectedLister
import org.loopring.looprwallet.core.presenters.SearchViewPresenter
import org.loopring.looprwallet.core.presenters.SearchViewPresenter.OnSearchViewChangeListener
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.createorder.activities.CreateOrderActivity
import org.loopring.looprwallet.createtransfer.activities.CreateTransferActivity
import org.loopring.looprwallet.homeorders.R
import org.loopring.looprwallet.tradedetails.activities.TradingPairDetailsActivity

/**
 * Created by Corey on 1/17/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class: To hold the children *Fragment*s' *ViewPager*s and allow them to be swiped
 * between.
 */
class HomeOrdersParentFragment : BaseTabFragment(), BottomNavigationReselectedLister,
        OnSearchViewChangeListener {

    override val layoutResource: Int
        get() = R.layout.fragment_orders_parent

    override val tabLayoutId: Int
        get() = R.id.ordersTabs

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
        return fragmentView.inflate(R.layout.appbar_orders, false) as? AppBarLayout
    }

    override fun initializeFloatingActionButton(floatingActionButton: FloatingActionButton) {
        floatingActionButton.setImageResource(R.drawable.ic_add_white_24dp)
        floatingActionButton.setOnClickListener {
            activity?.let { CreateOrderActivity.route(it, null) }
        }
    }

    override fun getAdapterContent(): List<Pair<String, BaseFragment>> {
        return listOf(
                Pair(str(R.string.open), HomeOpenOrdersFragment()),
                Pair(str(R.string.filled), HomeFilledOrdersFragment()),
                Pair(str(R.string.cancelled), HomeCancelledOrdersFragment())
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        BarcodeCaptureActivity.handleActivityResult(requestCode, resultCode, data) { type, value ->
            when(type) {
                BarcodeCaptureActivity.TYPE_PUBLIC_KEY -> {
                    CreateTransferActivity.route(this, value)
                }
                BarcodeCaptureActivity.TYPE_TRADING_PAIR -> {
                    val tradingPair = TradingPair.createFromMarket(value)
                    TradingPairDetailsActivity.route(tradingPair, this)
                }
            }
        }
    }

    override fun onBottomNavigationReselected() {
        logd("Orders reselected")
        val fragment = adapter.getItem(viewPager.currentItem)
        (fragment as BottomNavigationReselectedLister).onBottomNavigationReselected()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(org.loopring.looprwallet.core.R.menu.menu_home_search, menu)

        val searchItem = menu.findItem(R.id.menuMainSearch)
        val searchView = searchItem.actionView as SearchView

        searchViewPresenter.setupSearchView(searchItem, searchView)
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        android.R.id.home -> activity?.onOptionsItemSelected(item) ?: false
        R.id.menuMainScanQrCode -> {
            BarcodeCaptureActivity.route(this, arrayOf(BarcodeCaptureActivity.TYPE_PUBLIC_KEY, BarcodeCaptureActivity.TYPE_TRADING_PAIR))
            true
        }
        R.id.menuMainSettings -> {
            SettingsActivity.route(this)
            true
        }
        else -> super.onOptionsItemSelected(item)
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