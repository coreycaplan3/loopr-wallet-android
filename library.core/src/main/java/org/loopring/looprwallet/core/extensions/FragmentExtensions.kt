package org.loopring.looprwallet.core.extensions

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View

/**
 * Created by Corey on 1/19/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class: To create utility methods for commonly-done things, to streamline code and
 * make it more readable
 *
 */

/**
 * Attempts to find a fragment in this [FragmentManager] by [tag]. If the fragment with the
 * provided [tag] is **NOT** found, the creation function is invoked.
 *
 * @param tag The tag that's used to locate or create the fragment
 * @param block The block that will be executed, passing in the [tag], if the fragment was not
 * found.
 */
inline fun <reified T : Fragment> FragmentManager.findFragmentByTagOrCreate(tag: String, block: (String) -> T): T {
    return (this.findFragmentByTag(tag) as? T) ?: block(tag)
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T : View> Fragment.findViewById(@IdRes idRes: Int): T? = view?.findViewById(idRes)