package com.caplaninnovations.looprwallet.transitions

import android.animation.*
import android.support.transition.TransitionValues
import android.support.transition.Visibility
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.fragments.BaseTabFragment
import com.caplaninnovations.looprwallet.utilities.*

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
                    allowEnterTransitionOverlap = false
                    allowReturnTransitionOverlap = false

                    enterTransition = TabTransition()
                            .addMode(Visibility.MODE_IN)
                            .addTarget(tabLayoutTransitionName)

                    exitTransition = TabTransition()
                            .addMode(Visibility.MODE_OUT)
                            .addTarget(tabLayoutTransitionName) as TabTransition
                }
    }

    fun addMode(mode: Int): TabTransition {
        this.mode = mode
        return this
    }

    override fun onAppear(sceneRoot: ViewGroup?, view: View?, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        logd("Tabs appearing...")

        return view?.context?.resources?.let {
            val height = view.context!!.getResourceIdFromAttrId(android.R.attr.actionBarSize)
            val animator = ValueAnimator.ofFloat(0F, it.getDimension(height))
                    .setDuration(it.getInteger(R.integer.tab_layout_animation_duration).toLong())

            animator.addUpdateListener {
                view.layoutParams.height = (it.animatedValue as Float).toInt()
                view.layoutParams = view.layoutParams
            }

            animator.interpolator = DecelerateInterpolator()

            animator
        }
    }

    override fun onDisappear(sceneRoot: ViewGroup?, view: View?, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        logd("Tabs disappearing...")

        return view?.context?.resources?.let {
            view.pivotY = 0F
            val animator = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1F, 0F)
                    .setDuration(it.getInteger(R.integer.tab_layout_animation_duration).toLong())

            animator.interpolator = DecelerateInterpolator()

            animator
        }
    }

}