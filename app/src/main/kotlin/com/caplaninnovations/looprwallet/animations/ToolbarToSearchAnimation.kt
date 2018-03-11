package com.caplaninnovations.looprwallet.animations

import android.animation.Animator
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.view.animation.AlphaAnimation
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewAnimationUtils
import android.support.v4.content.ContextCompat
import android.view.animation.Animation
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.utilities.isLollipop
import com.caplaninnovations.looprwallet.utilities.isRtl


/**
 * Created by Corey Caplan on 3/11/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@SuppressLint("PrivateResource", "NewApi")
object ToolbarToSearchAnimation {

    /**
     * Animates the toolbar to go from a toolbar to a search view.
     */
    fun animateToSearch(fragment: BaseFragment, numberOfMenuIcon: Int, containsOverflow: Boolean) {
        val activity = fragment.activity!!
        val toolbar = fragment.toolbar!!

        toolbar.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.white))

        if (isLollipop()) {
            activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.quantum_grey_600)


            val endRadius = toolbar.width - getOverflowWidth(containsOverflow) - getMenuWidth(numberOfMenuIcon)
            val centerX = if (isRtl()) toolbar.width - endRadius else endRadius
            val centerY = toolbar.height / 2

            val createCircularReveal = ViewAnimationUtils.createCircularReveal(toolbar, centerX, centerY, 0.0f, endRadius.toFloat())
            createCircularReveal.duration = 250
            createCircularReveal.start()
        } else {
            val translateAnimation = TranslateAnimation(0.0f, 0.0f, -toolbar.height.toFloat(), 0.0f)
            translateAnimation.duration = 220
            toolbar.clearAnimation()
            toolbar.startAnimation(translateAnimation)
        }
    }

    fun animateToToolbar(fragment: BaseFragment, numberOfMenuIcon: Int, containsOverflow: Boolean) {
        val activity = fragment.activity!!
        val toolbar = fragment.toolbar!!

        if (isLollipop()) {
            activity.window.statusBarColor = getThemeColor(activity, R.attr.colorPrimaryDark)

            val width = toolbar.width - getOverflowWidth(containsOverflow) - getMenuWidth(numberOfMenuIcon)

            val centerX = if (isRtl()) toolbar.width - width else width
            val centerY = toolbar.height / 2

            val createCircularReveal = ViewAnimationUtils.createCircularReveal(toolbar, centerX, centerY, width.toFloat(), 0.0f)
            createCircularReveal.duration = 250
            createCircularReveal.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    toolbar.setBackgroundColor(getThemeColor(activity, R.attr.colorPrimary))
                }
            })
            createCircularReveal.start()
        } else {
            val alphaAnimation = AlphaAnimation(1.0f, 0.0f)
            val translateAnimation = TranslateAnimation(0.0f, 0.0f, 0.0f, -toolbar.height.toFloat())
            val animationSet = AnimationSet(true)
            animationSet.addAnimation(alphaAnimation)
            animationSet.addAnimation(translateAnimation)
            animationSet.duration = 220
            animationSet.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    toolbar.setBackgroundColor(getThemeColor(activity, R.attr.colorPrimary))
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
            toolbar.startAnimation(animationSet)
        }
    }

    private fun getOverflowWidth(containsOverflow: Boolean): Int {
        val resources = LooprWalletApp.getContext().resources
        return if (containsOverflow) {
            resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material)
        } else {
            0
        }
    }

    private fun getMenuWidth(numberOfMenuIcon: Int): Int {
        val resources = LooprWalletApp.getContext().resources
        val buttonWidth = resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)
        return buttonWidth * numberOfMenuIcon / 2
    }

    private fun getThemeColor(context: Context, id: Int): Int {
        val a = context.theme.obtainStyledAttributes(intArrayOf(id))
        val result = a.getColor(0, 0)
        a.recycle()
        return result
    }

}