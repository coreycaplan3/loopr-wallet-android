package com.caplaninnovations.looprwallet.fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.android.fragments.LooprFragmentPagerAdapter
import com.caplaninnovations.looprwallet.handlers.BottomNavigationHandler
import com.caplaninnovations.looprwallet.utilities.getAttrColorStateList
import com.caplaninnovations.looprwallet.utilities.logv
import kotlinx.android.synthetic.main.fragment_general_with_view_pager.*

/**
 * Created by Corey Caplan on 1/24/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
abstract class BaseTabFragment : BaseFragment() {

    override var container: ViewGroup? = null
        get() {
            val adapter = generalFragmentViewPager?.adapter as? LooprFragmentPagerAdapter
            val position = tabLayout?.selectedTabPosition
            return when (position) {
                is Int -> adapter?.getItem(position)?.container
                else -> null
            }
        }

    abstract var tabLayout: TabLayout?

    private lateinit var adapter: LooprFragmentPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = LooprFragmentPagerAdapter(childFragmentManager, getAdapterContent())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        generalFragmentViewPager.adapter = adapter
        tabLayout?.setupWithViewPager(generalFragmentViewPager)

        tabLayout?.tabTextColors = context?.getAttrColorStateList(R.attr.tabWidgetTextColor)
    }

    abstract fun getAdapterContent(): List<Pair<String, BaseFragment>>

    override fun onResume() {
        super.onResume()
        logv("Showing tab layout...")

        (activity as? BottomNavigationHandler.OnTabVisibilityChangeListener)?.onShowTabLayout(tabLayout)
    }

    override fun onPause() {
        super.onPause()
        logv("Hiding tab layout...")

        (activity as? BottomNavigationHandler.OnTabVisibilityChangeListener)?.onHideTabLayout(tabLayout)
    }

}