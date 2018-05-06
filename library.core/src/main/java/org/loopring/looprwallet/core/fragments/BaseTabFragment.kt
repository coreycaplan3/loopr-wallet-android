package org.loopring.looprwallet.core.fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewGroupCompat
import android.support.v4.view.ViewPager
import android.view.View
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.extensions.getAttrColorStateList
import org.loopring.looprwallet.core.models.android.fragments.LooprFragmentPagerAdapter

/**
 * Created by Corey Caplan on 1/24/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
abstract class BaseTabFragment : BaseFragment() {

    val tabLayoutTransitionName
        get() = "tab-transition-$tag"

    abstract val tabLayoutId: Int

    private var tabLayout: TabLayout? = null

    lateinit var viewPager: ViewPager

    lateinit var adapter: LooprFragmentPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = LooprFragmentPagerAdapter(childFragmentManager, getAdapterContent())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.fragmentContainer)
        viewPager.offscreenPageLimit = 4
        viewPager.adapter = adapter

        tabLayout = view.findViewById(tabLayoutId)

        ViewGroupCompat.setTransitionGroup(tabLayout, true)
        ViewCompat.setTransitionName(tabLayout, tabLayoutTransitionName)

        tabLayout?.setupWithViewPager(viewPager)
        tabLayout?.tabTextColors = context?.theme?.getAttrColorStateList(R.attr.tabTextColor)

        toolbarDelegate?.isToolbarCollapseEnabled = true
    }

    abstract fun getAdapterContent(): List<Pair<String, BaseFragment>>

}