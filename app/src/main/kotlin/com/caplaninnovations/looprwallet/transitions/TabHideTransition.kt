package com.caplaninnovations.looprwallet.transitions

import android.animation.Animator
import android.support.transition.Transition
import android.support.transition.TransitionValues
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.utilities.animateFromHeight
import com.caplaninnovations.looprwallet.utilities.getResourceIdFromAttrId

/**
 * Created by Corey Caplan on 2/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class TabHideTransition : Transition() {

    override fun captureStartValues(transitionValues: TransitionValues) {
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
    }

    override fun isTransitionRequired(startValues: TransitionValues?, endValues: TransitionValues?): Boolean {
        return true
    }

    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        val context = sceneRoot.context
        return context?.let {
            val actionBarSize = it.getResourceIdFromAttrId(android.R.attr.actionBarSize)
            endValues?.view?.animateFromHeight(actionBarSize, R.integer.tab_layout_animation_duration)
        }
    }

}