package org.loopring.looprwallet.core.models.android.fragments

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.ViewGroup
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
class LooprFragmentSwitcherPagerAdapter(
        container: ViewGroup,
        savedInstanceState: Bundle?,
        classLoader: ClassLoader?,
        private val fragmentManager: FragmentManager,
        private val fragmentList: List<Pair<String, BaseFragment>>
) {

    companion object {
        val TAG: String = LooprFragmentSwitcherPagerAdapter::class.java.simpleName

        private const val KEY_CURRENT_FRAGMENT_INDEX = "_CURRENT_FRAGMENT_INDEX"
    }

    lateinit var currentFragment: Fragment

    private val mSavedState = ArrayList<Fragment.SavedState?>()
    private val mFragments = ArrayList<Fragment?>()

    val count: Int
        get() = fragmentList.size

    fun getPageTitle(position: Int): CharSequence? = fragmentList[position].first

    init {
        when (savedInstanceState) {
            null -> setupAdapter(container)
            else -> restoreAdapter(savedInstanceState, classLoader!!)
        }
    }

    /**
     * Instantiates a given fragment at [position] and removes the old one and saves its state
     */
    fun instantiateFragment(container: ViewGroup, position: Int): Fragment {

        val oldPosition = fragmentList.indexOfFirst { it.second.tag == currentFragment.tag }
        saveOldFragmentState(oldPosition, currentFragment)

        val fragment = when {
            mFragments[position] != null ->
                // The item was already instantiated
                mFragments[position]!!
            else ->
                // We need to create the item
                getItem(position)
        }

        logd("Adding item #$position: f=$fragment")
        currentFragment = fragment
        mFragments[position] = fragment
        mSavedState[position]?.let {
            fragment.setInitialSavedState(it)
        }

        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(container.id, fragment, fragmentList[position].first)
                .disallowAddToBackStack()
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

            if (fragment === currentFragment) {
                state.putInt(KEY_CURRENT_FRAGMENT_INDEX, index)
            }
        }

        return state
    }

    // MARK - Private Methods

    /**
     * @return The Fragment associated with a specified position.
     */
    private fun getItem(position: Int): BaseFragment = fragmentList[position].second

    private fun setupAdapter(container: ViewGroup) {
        currentFragment = fragmentList.first().second
        for (fragments in fragmentList) {
            mSavedState.add(null)
            mFragments.add(null)
        }

        // Add the fragments
        val pair = fragmentList.first()
        fragmentManager.beginTransaction()
                .add(container.id, pair.second, pair.first)
                .disallowAddToBackStack()
                .commitNow()

        mFragments[0] = pair.second
    }

    private fun restoreAdapter(savedInstanceState: Parcelable, classLoader: ClassLoader) {
        val bundle = savedInstanceState as Bundle
        bundle.classLoader = classLoader

        fragmentList.forEach {
            mSavedState.add(null)
            mFragments.add(null)
        }

        bundle.getParcelableArray("states")?.forEachIndexed { index, savedState ->
            mSavedState[index] = savedState as? Fragment.SavedState?
        }

        bundle.keySet().forEach { key ->
            if (key.startsWith("f")) {
                val index = key.substring(1).toInt()
                val fragment = fragmentManager.getFragment(bundle, key)
                when {
                    fragment != null -> {
                        mFragments[index] = fragment
                    }
                    else -> logw("Bad fragment at key $key")
                }
            }
        }

        currentFragment = mFragments[bundle.getInt(KEY_CURRENT_FRAGMENT_INDEX, -1)]!!
    }

}