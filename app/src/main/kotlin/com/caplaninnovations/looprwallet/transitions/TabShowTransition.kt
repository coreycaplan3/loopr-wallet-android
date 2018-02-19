package com.caplaninnovations.looprwallet.transitions

import android.animation.Animator
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.design.widget.TabLayout
import android.transition.Transition
import android.transition.TransitionValues
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.utilities.animateFromHeight
import com.caplaninnovations.looprwallet.utilities.animateToHeight
import com.caplaninnovations.looprwallet.utilities.getResourceIdFromAttrId

/**
 * Created by Corey Caplan on 2/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
class TabShowTransition : Transition() {

    override fun captureStartValues(transitionValues: TransitionValues?) {
    }

    override fun captureEndValues(transitionValues: TransitionValues?) {
    }

    override fun createAnimator(sceneRoot: ViewGroup?, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        return if (startValues?.view is TabLayout) {
            val context = sceneRoot?.context
            context?.let {
                val actionBarSize = it.getResourceIdFromAttrId(android.R.attr.actionBarSize)
                startValues.view.animateToHeight(actionBarSize, R.integer.tab_layout_animation_duration)
            }
        } else {
            null
        }
    }

}