package org.loopring.looprwallet.core.transitions

import android.animation.Animator
import android.support.transition.TransitionValues
import android.support.transition.Visibility
import android.view.View
import android.view.ViewGroup
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.extensions.animateScaleBoth

/**
 * Created by Corey on 3/9/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class FloatingActionButtonTransition : Visibility() {

    override fun captureStartValues(transitionValues: TransitionValues) {
        super.captureStartValues(transitionValues)
    }

    override fun onAppear(sceneRoot: ViewGroup?, view: View?, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        return view?.let {
            it.scaleX = 0F
            it.scaleY = 0F

            it.pivotX = it.resources.getDimension(R.dimen.fab_size) / 2
            it.pivotY = it.resources.getDimension(R.dimen.fab_size) / 2

            it.animateScaleBoth(1F, R.integer.fab_transition_duration)
        }
    }

    override fun onDisappear(sceneRoot: ViewGroup?, view: View?, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        return view?.let {
            it.pivotX = it.resources.getDimension(R.dimen.fab_size) / 2
            it.pivotY = it.resources.getDimension(R.dimen.fab_size) / 2

            it.animateScaleBoth(0F, R.integer.fab_transition_duration)
        }
    }

}