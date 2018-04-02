package com.caplaninnovations.looprwallet.models.android.fragments

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.extensions.logd

/**
 * Created by Corey Caplan on 1/24/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class LooprFragmentPagerAdapter(private val fragmentManager: FragmentManager,
                                private val fragmentList: List<Pair<String, BaseFragment>>)
    : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): BaseFragment = fragmentList[position].second

    override fun getCount(): Int = fragmentList.size

    override fun getPageTitle(position: Int): CharSequence? = fragmentList[position].first

    companion object {
        const val KEY_FRAGMENT = "_FRAGMENT"
    }

    override fun saveState(): Parcelable? {
        super.saveState()
        val bundle = Bundle()

        fragmentList.forEachIndexed { index, pair ->
            val fragment = pair.second
            try {
                val savedState = fragmentManager.saveFragmentInstanceState(fragment)
                bundle.putParcelable(KEY_FRAGMENT + index, savedState)
            } catch (e: Exception) {
                logd("Unable to save state for ${fragment.tag}")
            }
        }

        return bundle
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        (state as? Bundle)?.let {
            it.classLoader = loader

            fragmentList.forEachIndexed { index, pair ->
                val fragment = pair.second
                val bundle = it.getParcelable(KEY_FRAGMENT + index) as Fragment.SavedState
                try {
                    fragment.setInitialSavedState(bundle)
                } catch (ignored: Exception) {
                }
            }
        }
    }

}