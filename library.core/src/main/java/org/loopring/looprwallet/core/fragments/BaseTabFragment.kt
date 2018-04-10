package org.loopring.looprwallet.core.fragments

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewGroupCompat
import android.support.v4.view.ViewPager
import android.view.View
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.extensions.getAttrColorStateList
import org.loopring.looprwallet.core.models.android.fragments.LooprFragmentPagerAdapter
import org.loopring.looprwallet.core.transitions.TabTransition

/**
 * Created by Corey Caplan on 1/24/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
abstract class BaseTabFragment : BaseFragment() {

    companion object {

        const val KEY_APP_BAR_HEIGHT = "_APP_BAR_SCALE"
        private const val TAB_LAYOUT_DEFAULT_HEIGHT = 0
    }

    val tabLayoutTransitionName
        get() = "tab-transition-$tag"

    abstract val tabLayoutId: Int

    private var tabLayout: TabLayout? = null

    lateinit var viewPager: ViewPager

    lateinit var adapter: LooprFragmentPagerAdapter

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        TabTransition.setupForBaseTabFragment(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = LooprFragmentPagerAdapter(childFragmentManager, getAdapterContent())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.fragmentContainer)
        viewPager.adapter = adapter

        tabLayout = view.findViewById(tabLayoutId)

        tabLayout?.layoutParams?.height = savedInstanceState?.getInt(KEY_APP_BAR_HEIGHT, TAB_LAYOUT_DEFAULT_HEIGHT) ?: TAB_LAYOUT_DEFAULT_HEIGHT
        tabLayout?.layoutParams = tabLayout?.layoutParams
        ViewGroupCompat.setTransitionGroup(tabLayout, true)
        ViewCompat.setTransitionName(tabLayout, tabLayoutTransitionName)

        tabLayout?.setupWithViewPager(viewPager)
        tabLayout?.tabTextColors = context?.theme?.getAttrColorStateList(R.attr.tabTextColor)

        enableToolbarCollapsing()
    }

    abstract fun getAdapterContent(): List<Pair<String, BaseFragment>>

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(KEY_APP_BAR_HEIGHT, tabLayout?.height ?: TAB_LAYOUT_DEFAULT_HEIGHT)
    }

}