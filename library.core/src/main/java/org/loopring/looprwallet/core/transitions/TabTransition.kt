package org.loopring.looprwallet.core.transitions

import android.animation.Animator
import android.animation.ObjectAnimator
import android.support.transition.TransitionSet
import android.support.transition.TransitionValues
import android.support.transition.Visibility
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.extensions.addMode
import org.loopring.looprwallet.core.extensions.animateToHeight
import org.loopring.looprwallet.core.extensions.getResourceIdFromAttrId
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.fragments.BaseTabFragment

/**
 * Created by Corey Caplan on 2/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class TabTransition : Visibility() {

    companion object {

        fun setupForBaseTabFragment(baseTagFragment: BaseTabFragment) =
                baseTagFragment.apply {
                    (enterTransition as TransitionSet).addTransition(TabTransition()
                            .addMode(Visibility.MODE_IN)
                            .addTarget(tabLayoutTransitionName)
                    )

                    (exitTransition as TransitionSet).addTransition(
                            TabTransition()
                                    .addMode(Visibility.MODE_OUT)
                                    .addTarget(tabLayoutTransitionName) as TabTransition
                    )
                }
    }

    override fun onAppear(sceneRoot: ViewGroup?, view: View?, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        logd("Tabs appearing...")

        return view?.let {
            val height = it.context.theme.getResourceIdFromAttrId(android.R.attr.actionBarSize)
            it.animateToHeight(height, R.integer.fragment_transition_duration)
        }
    }

    override fun onDisappear(sceneRoot: ViewGroup?, view: View?, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        logd("Tabs disappearing...")

        return view?.context?.resources?.let {
            view.pivotY = 0F
            val animator = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1F, 0F)
                    .setDuration(it.getInteger(R.integer.fragment_transition_duration).toLong())

            animator.interpolator = DecelerateInterpolator()

            animator
        }
    }

}