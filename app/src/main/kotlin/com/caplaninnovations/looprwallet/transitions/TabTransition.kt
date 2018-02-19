package com.caplaninnovations.looprwallet.transitions

import android.animation.Animator
import android.annotation.TargetApi
import android.os.Build
import android.transition.Transition
import android.transition.TransitionListenerAdapter
import android.transition.TransitionValues
import android.transition.Visibility
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.utilities.*

/**
 * Created by Corey Caplan on 2/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
class TabTransition : Visibility() {

    override fun onAppear(sceneRoot: ViewGroup?, view: View?, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        logd("Tabs appearing...")

        return view?.context?.let {
            val actionBarSize = it.getResourceIdFromAttrId(android.R.attr.actionBarSize)
            view.animateToHeight(actionBarSize, R.integer.tab_layout_animation_duration)
        }
    }

    override fun onDisappear(sceneRoot: ViewGroup?, view: View?, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        logd("Tabs disappearing...")

        return view?.context?.let {
            val actionBarSize = it.getResourceIdFromAttrId(android.R.attr.actionBarSize)
            view.animateFromHeight(actionBarSize, R.integer.tab_layout_animation_duration)
        }
    }

}