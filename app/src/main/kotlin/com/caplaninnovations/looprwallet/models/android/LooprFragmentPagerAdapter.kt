package com.caplaninnovations.looprwallet.models.android

import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.utilities.str

/**
 * Created by Corey Caplan on 1/24/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
class LooprFragmentPagerAdapter(fm: FragmentManager, private val fragmentList: List<Pair<String, Fragment>>)
    : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = fragmentList[position].second

    override fun getCount(): Int = fragmentList.size

    override fun getPageTitle(position: Int): CharSequence? = fragmentList[position].first

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        // TODO
    }

    override fun saveState(): Parcelable? {
        return super.saveState()
    }

}