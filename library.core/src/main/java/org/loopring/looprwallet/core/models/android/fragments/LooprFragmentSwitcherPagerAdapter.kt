package org.loopring.looprwallet.core.models.android.fragments

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.ViewGroup
import org.loopring.looprwallet.core.extensions.ifNotNull
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.extensions.logv
import org.loopring.looprwallet.core.extensions.logw
import org.loopring.looprwallet.core.fragments.BaseFragment
import java.util.*

/**
 * Created by Corey Caplan on 1/24/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class LooprFragmentSwitcherPagerAdapter(private val fragmentManager: FragmentManager,
                                        private val fragmentList: List<Pair<String, BaseFragment>>) {

    companion object {

        val TAG: String = LooprFragmentSwitcherPagerAdapter::class.java.simpleName
    }

    var currentFragment: Fragment? = null

    private val mSavedState = ArrayList<Fragment.SavedState?>()
    private val mFragments = ArrayList<Fragment?>()

    init {
        for (fragments in fragmentList) {
            mFragments.add(null)
            mSavedState.add(null)
        }
    }

    /**
     * Instantiates a given fragment at [position] and removes the old one and saves its state
     */
    fun instantiateFragment(container: ViewGroup, position: Int): Fragment {

        currentFragment?.ifNotNull { oldFragment ->
            val oldPosition = fragmentList.indexOfFirst { it.second.tag == oldFragment.tag }
            saveOldFragmentState(oldPosition, oldFragment)
        }

        val fragment = when {
            mFragments[position] != null ->
                // The item was already instantiated
                mFragments[position]!!

            else ->
                // We need to create the item
                getItem(position)
        }
        fragment.setMenuVisibility(true)
        currentFragment = fragment

        logd("Adding item #$position: f=$fragment")

        mSavedState[position]?.ifNotNull { fragment.setInitialSavedState(it) }

        mFragments[position] = fragment

        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(container.id, fragment, getPageTitle(position)!!.toString())
                .commitNow()

        return fragment
    }

    private fun saveOldFragmentState(oldPosition: Int, oldFragment: Fragment) {
        logv("Removing item #$oldPosition: f=$oldFragment v=${oldFragment.view}")

        val savedState = when {
            oldFragment.isAdded -> fragmentManager.saveFragmentInstanceState(oldFragment)
            else -> null
        }
        mSavedState[oldPosition] = savedState
        mFragments[oldPosition] = null
    }

    fun saveState(): Parcelable? {
        val state = Bundle()
        if (mSavedState.size > 0) {
            val fss = arrayOfNulls<Fragment.SavedState>(mSavedState.size)
            mSavedState.toArray(fss)
            state.putParcelableArray("states", fss)
        }

        mFragments.forEachIndexed { index, fragment ->
            if (fragment != null && fragment.isAdded) {
                val key = "f$index"
                fragmentManager.putFragment(state, key, fragment)
            }
        }

        return state
    }

    fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        if (state == null) {
            // Guard
            return
        }

        val bundle = state as Bundle
        bundle.classLoader = loader
        mSavedState.clear()
        mFragments.clear()

        bundle.getParcelableArray("states")?.forEach { savedState ->
            mSavedState.add(savedState as? Fragment.SavedState?)
        }

        bundle.keySet().forEach { key ->
            if (key.startsWith("f")) {
                val index = key.substring(1).toInt()
                val fragment = fragmentManager.getFragment(bundle, key)
                when {
                    fragment != null -> {
                        mFragments.add(index, fragment)
                    }
                    else -> logw("Bad fragment at key $key")
                }
            }
        }
    }

    /**
     * @return The Fragment associated with a specified position.
     */
    private fun getItem(position: Int): BaseFragment = fragmentList[position].second

    val count: Int
        get() = fragmentList.size

    fun getPageTitle(position: Int): CharSequence? = fragmentList[position].first

}