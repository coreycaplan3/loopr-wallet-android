package com.caplaninnovations.looprwallet.models.android.fragments

import android.support.annotation.*
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.extensions.logv


/**
 * Created by Corey on 1/19/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class: A controller used to standardize fragment transactions.
 */
class FragmentTransactionController(@IdRes private val container: Int,
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
    var enterAnimation: Int = 0

    @AnimRes
    @AnimatorRes
    var exitAnimation: Int = 0

    @AnimRes
    @AnimatorRes
    var popEnterAnimation: Int = 0

    @AnimRes
    @AnimatorRes
    var popExitAnimation: Int = 0

    @StyleRes
    var transitionStyle: Int? = null

    /**
     * Creates an animation for sliding the entering fragment up and sliding it down when it's
     * popped. The other animations are nullified.
     */
    fun slideUpAndDownAnimation() {
        enterAnimation = R.anim.slide_up
        popExitAnimation = R.anim.slide_down

        popEnterAnimation = 0
        exitAnimation = 0
    }

    /**
     * Creates a transaction, replaces the current container & commits it via a call to
     * [FragmentTransaction.commitAllowingStateLoss]. Reason being, it's okay to lose the commit,
     * since only the user is capable of disrupting the commit.
     */
    fun commitTransaction(fragmentManager: FragmentManager) {
        val transaction = fragmentManager.beginTransaction()

        val oldFragment = fragmentManager.findFragmentById(R.id.activityContainer)
        val text = oldFragment?.tag?.plus(" fragment") ?: "container"
        logv("Replacing $text with $newFragmentTag fragment...")

        transitionStyle?.let { transaction.setTransitionStyle(it) }

        transition?.let { transaction.setTransition(it) }
        sharedElements?.let { it.forEach { transaction.addSharedElement(it.first, it.second) } }

        transaction.setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation)

        // It's okay to commit allowing state loss, since the user disrupted the delayed commit.
        // Sometimes, we delay the commit which means we may commit after the state has been saved.
        transaction.replace(container, newFragment, newFragmentTag)
                .addToBackStack(newFragmentTag)
                .commitAllowingStateLoss()
    }

}