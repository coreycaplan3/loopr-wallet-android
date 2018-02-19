package com.caplaninnovations.looprwallet.fragments

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewGroupCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.android.fragments.LooprFragmentPagerAdapter
import com.caplaninnovations.looprwallet.utilities.getAttrColorStateList
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

    abstract val tabLayoutName: String

    abstract val tabLayoutId: Int

    var tabLayout: TabLayout? = null
        private set

    private lateinit var adapter: LooprFragmentPagerAdapter

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        allowEnterTransitionOverlap = false
        postponeEnterTransition()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = LooprFragmentPagerAdapter(childFragmentManager, getAdapterContent())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentContainer.adapter = adapter

        tabLayout = view.findViewById(tabLayoutId)
        ViewGroupCompat.setTransitionGroup(tabLayout, true)
        ViewCompat.setTransitionName(tabLayout, tabLayoutName)
        tabLayout?.setupWithViewPager(fragmentContainer)
        tabLayout?.tabTextColors = context?.getAttrColorStateList(R.attr.bottomNavigationTabTextColor)
    }

    abstract fun getAdapterContent(): List<Pair<String, BaseFragment>>

}