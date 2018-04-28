package org.loopring.looprwallet.core.models.android.fragments

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup
import org.loopring.looprwallet.core.fragments.BaseFragment

/**
 * Created by Corey Caplan on 1/24/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class LooprFragmentStatePagerAdapter(fragmentManager: FragmentManager,
                                     private val fragmentList: List<Pair<String, BaseFragment>>)
    : FragmentStatePagerAdapter(fragmentManager) {

    var currentFragment: Fragment? = null
        private set

    override fun getItem(position: Int): BaseFragment = fragmentList[position].second

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        if (`object` is Fragment) {
            currentFragment = `object`
        }
        super.setPrimaryItem(container, position, `object`)
    }

    override fun getCount(): Int = fragmentList.size

    override fun getPageTitle(position: Int): CharSequence? = fragmentList[position].first

}