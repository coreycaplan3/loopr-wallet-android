package org.loopring.looprwallet.homemarkets.fragments

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.ViewGroup
import org.loopring.looprwallet.barcode.activities.BarcodeCaptureActivity
import org.loopring.looprwallet.barcode.activities.BarcodeCaptureActivity.Companion.TYPE_PUBLIC_KEY
import org.loopring.looprwallet.barcode.activities.BarcodeCaptureActivity.Companion.TYPE_TRADING_PAIR
import org.loopring.looprwallet.core.activities.SettingsActivity
import org.loopring.looprwallet.core.extensions.getResourceIdFromAttrId
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.fragments.BaseTabFragment
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter.BottomNavigationReselectedLister
import org.loopring.looprwallet.core.presenters.SearchViewPresenter
import org.loopring.looprwallet.core.presenters.SearchViewPresenter.OnSearchViewChangeListener
import org.loopring.looprwallet.core.presenters.SearchViewPresenter.SearchFragment
import org.loopring.looprwallet.core.utilities.ApplicationUtility.col
import org.loopring.looprwallet.core.utilities.ApplicationUtility.dimen
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.createtransfer.activities.CreateTransferActivity
import org.loopring.looprwallet.homemarkets.R
import org.loopring.looprwallet.tradedetails.activities.TradingPairDetailsActivity
import org.loopring.looprwallet.wrapeth.activities.WrapEthActivity
import kotlin.math.roundToInt

/**
 * Created by Corey on 1/17/2018.
 *
 *  Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class HomeMarketsParentFragment : BaseTabFragment(), BottomNavigationReselectedLister,
        SearchFragment, OnSearchViewChangeListener {

    override val layoutResource: Int
        get() = R.layout.fragment_markets_parent

    override val tabLayoutId: Int
        get() = R.id.marketsTabs

    override lateinit var searchViewPresenter: SearchViewPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbarDelegate?.onCreateOptionsMenu = createOptionsMenu
        toolbarDelegate?.onOptionsItemSelected = optionsItemSelected

        searchViewPresenter = SearchViewPresenter(
                containsOverflowMenu = true,
                numberOfVisibleMenuItems = 1,
                baseFragment = this,
                savedInstanceState = savedInstanceState,
                listener = this
        )
    }

    override fun createAppbarLayout(fragmentView: ViewGroup): AppBarLayout {
        val tabLayout = TabLayout(fragmentView.context).apply {
            val theme = fragmentView.context.theme
            id = R.id.marketsTabs

            val height = dimen(theme.getResourceIdFromAttrId(R.attr.actionBarSize))
            layoutParams = AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT, height.roundToInt())
            background = ColorDrawable(col(theme.getResourceIdFromAttrId(R.attr.colorPrimary)))
            tabGravity = TabLayout.GRAVITY_FILL
            setSelectedTabIndicatorColor(col(theme.getResourceIdFromAttrId(R.attr.tabIndicatorColor)))
            setSelectedTabIndicatorHeight(dimen(R.dimen.tabIndicatorHeight).roundToInt())
            tabMode = TabLayout.MODE_FIXED
        }

        return super.createAppbarLayout(fragmentView).apply {
            addView(tabLayout)
        }
    }

    override fun initializeFloatingActionButton(floatingActionButton: FloatingActionButton) {
        floatingActionButton.setImageResource(R.drawable.ic_card_giftcard_white_24dp)
        floatingActionButton.setOnClickListener {
            WrapEthActivity.route(activity!!)
        }
    }

    override fun getAdapterContent(): List<Pair<String, BaseFragment>> {
        return listOf(
                Pair(str(R.string.all), HomeAllMarketsFragment()),
                Pair(str(R.string.favorites), HomeFavoriteMarketsFragment())
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        BarcodeCaptureActivity.handleActivityResult(requestCode, resultCode, data) { type, value ->
            when (type) {
                BarcodeCaptureActivity.TYPE_PUBLIC_KEY -> {
                    CreateTransferActivity.route(this, value)
                }
                BarcodeCaptureActivity.TYPE_TRADING_PAIR -> {
                    TradingPairDetailsActivity.route(TradingPair(value), this)
                }
            }
        }
    }

    private val createOptionsMenu: (Toolbar?) -> Unit = {
        it?.menu?.clear()
        it?.inflateMenu(R.menu.menu_home_search)

        if (it != null) {
            val searchItem = it.menu.findItem(R.id.menuMainSearch)
            val searchView = searchItem.actionView as SearchView
            searchViewPresenter.setupSearchView(searchItem, searchView)
        }
    }

    private val optionsItemSelected: (MenuItem?) -> Boolean = { item ->
        when (item?.itemId) {
            android.R.id.home -> activity?.onOptionsItemSelected(item) ?: false
            R.id.menuMainScanQrCode -> {
                BarcodeCaptureActivity.route(this, arrayOf(TYPE_PUBLIC_KEY, TYPE_TRADING_PAIR))
                true
            }
            R.id.menuMainSettings -> {
                SettingsActivity.route(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBottomNavigationReselected() {
        logd("Markets reselected")
        val fragment = adapter.getItem(viewPager.currentItem)
        (fragment as? BottomNavigationReselectedLister)?.onBottomNavigationReselected()
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
        for (i in 0 until adapter.count) {
            (adapter.getItem(i) as? OnSearchViewChangeListener)?.onQueryTextChangeListener(searchQuery)
        }
    }

    override fun onSearchItemExpanded() {
        toolbarDelegate?.removeAllOptionsMenuExceptSearch()

        for (i in 0 until adapter.count) {
            (adapter.getItem(i) as? OnSearchViewChangeListener)?.onSearchItemExpanded()
        }
    }

    override fun onSearchItemCollapsed() {
        for (i in 0 until adapter.count) {
            (adapter.getItem(i) as? OnSearchViewChangeListener)?.onSearchItemCollapsed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        searchViewPresenter.onSaveInstanceState(outState)
    }

}