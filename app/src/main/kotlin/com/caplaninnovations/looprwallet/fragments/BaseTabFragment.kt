package com.caplaninnovations.looprwallet.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewGroupCompat
import android.transition.Fade
import android.transition.Transition
import android.transition.Visibility
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

    abstract val tabLayoutName: String

    abstract val tabLayoutId: Int

    var tabLayout: TabLayout? = null
        private set

    private lateinit var adapter: LooprFragmentPagerAdapter

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (isLollipop()) {
            allowEnterTransitionOverlap = true
            allowReturnTransitionOverlap = true

            val tabEnterTransition = TabTransition().addTarget(tabLayoutName) as TabTransition
            tabEnterTransition.mode = Visibility.MODE_IN
            enterTransition = tabEnterTransition
            reenterTransition = tabEnterTransition

            val tabExitTransition = TabTransition().addTarget(tabLayoutName) as TabTransition
            tabExitTransition.mode = Visibility.MODE_OUT
            exitTransition = tabExitTransition
            returnTransition = tabExitTransition
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = LooprFragmentPagerAdapter(childFragmentManager, getAdapterContent())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentContainer.adapter = adapter

        tabLayout = view.findViewById(tabLayoutId)
        tabLayout?.layoutParams?.height = 0
        tabLayout?.layoutParams = tabLayout?.layoutParams

        ViewGroupCompat.setTransitionGroup(tabLayout, true)
        ViewCompat.setTransitionName(tabLayout, tabLayoutName)
        tabLayout?.setupWithViewPager(fragmentContainer)
        tabLayout?.tabTextColors = context?.getAttrColorStateList(R.attr.bottomNavigationTabTextColor)
    }


    abstract fun getAdapterContent(): List<Pair<String, BaseFragment>>

}