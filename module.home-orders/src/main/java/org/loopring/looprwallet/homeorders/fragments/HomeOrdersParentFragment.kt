package org.loopring.looprwallet.homeorders.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.graphics.drawable.DrawableCompat
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
import org.loopring.looprwallet.core.utilities.ApplicationUtility
import org.loopring.looprwallet.core.utilities.ApplicationUtility.drawable
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.createorder.activities.CreateOrderActivity
import org.loopring.looprwallet.createtransfer.activities.CreateTransferActivity
import org.loopring.looprwallet.homeorders.R
import org.loopring.looprwallet.tradedetails.activities.TradingPairDetailsActivity
import kotlin.math.roundToInt

/**
 * Created by Corey on 1/17/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class: To hold the children *Fragment*s' *ViewPager*s and allow them to be swiped
 * between.
 */
class HomeOrdersParentFragment : BaseTabFragment(), BottomNavigationReselectedLister,
        SearchViewPresenter.SearchFragment, OnSearchViewChangeListener {

    override val layoutResource: Int
        get() = R.layout.fragment_orders_parent

    override val tabLayoutId: Int
        get() = R.id.ordersTabs

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
            id = R.id.ordersTabs

            val height = ApplicationUtility.dimen(theme.getResourceIdFromAttrId(R.attr.actionBarSize))
            layoutParams = AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT, height.roundToInt())
            background = ColorDrawable(ApplicationUtility.col(theme.getResourceIdFromAttrId(R.attr.colorPrimary)))
            tabGravity = TabLayout.GRAVITY_FILL
            setSelectedTabIndicatorColor(ApplicationUtility.col(theme.getResourceIdFromAttrId(R.attr.tabIndicatorColor)))
            setSelectedTabIndicatorHeight(ApplicationUtility.dimen(R.dimen.tabIndicatorHeight).roundToInt())
            tabMode = TabLayout.MODE_FIXED
        }

        return super.createAppbarLayout(fragmentView).apply {
            addView(tabLayout)
        }
    }

    override fun initializeFloatingActionButton(floatingActionButton: FloatingActionButton) {
        val drawable = drawable(R.drawable.ic_add_white_24dp)
        DrawableCompat.setTint(drawable, Color.WHITE)
        floatingActionButton.setImageDrawable(drawable)
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

    override fun onBottomNavigationReselected() {
        logd("Orders reselected")
        val fragment = adapter.getItem(viewPager.currentItem)
        (fragment as BottomNavigationReselectedLister).onBottomNavigationReselected()
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
            (adapter.getItem(i) as OnSearchViewChangeListener).onQueryTextChangeListener(searchQuery)
        }
    }

    override fun onSearchItemExpanded() {
        toolbarDelegate?.removeAllOptionsMenuExceptSearch()

        for (i in 0 until adapter.count) {
            (adapter.getItem(i) as OnSearchViewChangeListener).onSearchItemExpanded()
        }
    }

    override fun onSearchItemCollapsed() {
        for (i in 0 until adapter.count) {
            (adapter.getItem(i) as OnSearchViewChangeListener).onSearchItemCollapsed()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        searchViewPresenter.onSaveInstanceState(outState)
    }

}