package com.caplaninnovations.looprwallet.fragments

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.android.fragments.LooprFragmentPagerAdapter
import com.caplaninnovations.looprwallet.handlers.BottomNavigationHandler
import com.caplaninnovations.looprwallet.utilities.getAttrColorStateList
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

    abstract val tabLayoutResource: Int

    lateinit var tabLayout: TabLayout
        private set

    private lateinit var adapter: LooprFragmentPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = LooprFragmentPagerAdapter(childFragmentManager, getAdapterContent())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentContainer.adapter = adapter

        tabLayout = view.findViewById(tabLayoutResource)
        tabLayout.setupWithViewPager(fragmentContainer)
        tabLayout.tabTextColors = context?.getAttrColorStateList(R.attr.bottomNavigationTabTextColor)
    }

    abstract fun getAdapterContent(): List<Pair<String, BaseFragment>>

}