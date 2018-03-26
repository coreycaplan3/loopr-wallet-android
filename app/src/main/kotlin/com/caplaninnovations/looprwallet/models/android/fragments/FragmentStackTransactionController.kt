package com.caplaninnovations.looprwallet.models.android.fragments

import android.support.annotation.*
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.view.View
import com.caplaninnovations.looprwallet.extensions.allNonNull
import com.caplaninnovations.looprwallet.extensions.isBothNonNull
import com.caplaninnovations.looprwallet.extensions.logv


/**
 * Created by Corey on 1/19/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class FragmentStackTransactionController(@IdRes private val container: Int,
                                         private val newFragment: Fragment,
                                         private val newFragmentTag: String) {

    @IntDef(
            FragmentTransaction.TRANSIT_NONE,
            FragmentTransaction.TRANSIT_FRAGMENT_OPEN,
            FragmentTransaction.TRANSIT_FRAGMENT_CLOSE,
            FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    @Retention(AnnotationRetention.SOURCE)
    internal annotation class FragmentTransition

    var sharedElements: List<Pair<View, String>>? = null

    @FragmentTransition
    var transition: Int? = null

    @AnimRes
    @AnimatorRes
    var enterAnimation: Int? = null

    @AnimRes
    @AnimatorRes
    var exitAnimation: Int? = null

    @AnimRes
    @AnimatorRes
    var popEnterAnimation: Int? = null

    @AnimRes
    @AnimatorRes
    var popExitAnimation: Int? = null

    @StyleRes
    var transitionStyle: Int? = null

    /**
     * Creates a transaction, replaces the current container & commits it via a call to
     * [FragmentTransaction.commitAllowingStateLoss]. Reason being, it's okay to lose the commit,
     * since the only user is the one who may disrupt the commit.
     */
    fun commitTransaction(fragmentManager: FragmentManager, oldFragment: Fragment?) {
        val transaction = fragmentManager.beginTransaction()

        val text = oldFragment?.tag?.plus(" fragment") ?: "container"
        logv("Replacing $text with $newFragmentTag fragment...")

        transition?.let { transaction.setTransition(it) }

        transaction.replace(container, newFragment, newFragmentTag)

        Pair(enterAnimation, exitAnimation).allNonNull { enterExitPair ->
            val popEnterExitPair = Pair(popEnterAnimation, popExitAnimation)

            if (popEnterExitPair.isBothNonNull()) {
                popEnterExitPair.allNonNull {
                    transaction.setCustomAnimations(enterExitPair.first, enterExitPair.second, it.first, it.second)
                }
            } else {
                transaction.setCustomAnimations(enterExitPair.first, enterExitPair.second)
            }
        }

        if (!Pair(enterAnimation, exitAnimation).isBothNonNull()) {
            transaction.setCustomAnimations(0, 0, 0, 0)
        }

        transitionStyle?.let { transaction.setTransitionStyle(it) }

        sharedElements?.let { it.forEach { transaction.addSharedElement(it.first, it.second) } }

        transaction.addToBackStack(newFragmentTag)

        // It's okay to lose the commit, since the user disrupted the delayed commit. The delay is
        // only 125ms so it should RARELY EVER happen
        transaction.commitAllowingStateLoss()
    }

}