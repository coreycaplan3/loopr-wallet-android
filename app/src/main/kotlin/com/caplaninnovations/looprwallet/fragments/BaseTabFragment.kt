package com.caplaninnovations.looprwallet.fragments

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewGroupCompat
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.android.fragments.LooprFragmentPagerAdapter
import com.caplaninnovations.looprwallet.transitions.TabTransition
import com.caplaninnovations.looprwallet.utilities.*
import kotlinx.android.synthetic.main.fragment_markets_parent.*

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

    abstract val tabLayoutTransitionName: String

    abstract val tabLayoutId: Int

    private var tabLayout: TabLayout? = null

    private lateinit var adapter: LooprFragmentPagerAdapter

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

        fragmentContainer.adapter = adapter

        tabLayout = view.findViewById(tabLayoutId)

        tabLayout?.layoutParams?.height = savedInstanceState?.getInt(KEY_APP_BAR_HEIGHT, TAB_LAYOUT_DEFAULT_HEIGHT) ?: TAB_LAYOUT_DEFAULT_HEIGHT
        tabLayout?.layoutParams = tabLayout?.layoutParams
        ViewGroupCompat.setTransitionGroup(tabLayout, true)
        ViewCompat.setTransitionName(tabLayout, tabLayoutTransitionName)

        tabLayout?.setupWithViewPager(fragmentContainer)
        tabLayout?.tabTextColors = context?.getAttrColorStateList(R.attr.tabTextColor)

        enableToolbarCollapsing()
    }

    abstract fun getAdapterContent(): List<Pair<String, BaseFragment>>

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(KEY_APP_BAR_HEIGHT, tabLayout?.height ?: TAB_LAYOUT_DEFAULT_HEIGHT)
    }

}