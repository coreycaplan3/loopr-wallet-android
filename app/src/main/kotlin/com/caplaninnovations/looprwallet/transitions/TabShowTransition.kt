package com.caplaninnovations.looprwallet.transitions

import android.animation.Animator
import android.support.transition.Transition
import android.support.transition.TransitionValues
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.utilities.animateToHeight
import com.caplaninnovations.looprwallet.utilities.getResourceIdFromAttrId
import com.caplaninnovations.looprwallet.utilities.loge

/**
 * Created by Corey Caplan on 2/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class TabShowTransition : Transition() {

    override fun captureStartValues(transitionValues: TransitionValues) {
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
    }

    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        val context = sceneRoot.context
        return context?.let {
            val actionBarSize = it.getResourceIdFromAttrId(android.R.attr.actionBarSize)
            endValues?.view?.animateToHeight(actionBarSize, R.integer.tab_layout_animation_duration)
        }
    }

}