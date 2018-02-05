package com.caplaninnovations.looprwallet.fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.android.fragments.LooprFragmentPagerAdapter
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationHandler
import com.caplaninnovations.looprwallet.utilities.getAttrColorStateList
import com.caplaninnovations.looprwallet.utilities.logv

/**
 * Created by Corey Caplan on 1/24/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
abstract class BaseTabFragment : BaseFragment() {

    override var container: ViewGroup? = null
        get() {
            val adapter = viewPager?.adapter as? LooprFragmentPagerAdapter
            val position = tabLayout?.selectedTabPosition
            return when(position) {
                is Int -> adapter?.getItem(position)?.container
                else -> null
            }
        }

    abstract var tabLayout: TabLayout?

    abstract var viewPager: ViewPager?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager?.adapter = LooprFragmentPagerAdapter(childFragmentManager, getAdapterContent())
        tabLayout?.setupWithViewPager(viewPager)

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