package com.caplaninnovations.looprwallet.utilities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.support.annotation.DimenRes
import android.support.annotation.IntegerRes
import android.transition.TransitionManager
import android.util.TypedValue
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import com.caplaninnovations.looprwallet.R

/**
 * Created by Corey on 1/18/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class: To create utility methods for commonly-done things, to streamline code and
 * make it more readable
 *
 */

/**
 * Animates the alpha of this view to the given value. Must be between 0 and 1.
 *
 * @param toValue The value to which the alpha will be animated. Must be between 0 and 1
 * (inclusive).
 */
fun View.animateAlpha(toValue: Float) {
    if (toValue < 0 || toValue > 1) {
        throw IllegalArgumentException("The inputted toValue must be between [0, 1]. Found: $toValue")
    }

    val animator = ValueAnimator.ofFloat(this.alpha, toValue)
    animator.addUpdateListener { this.alpha = it.animatedValue as Float }
    animator.duration = resources.getInteger(R.integer.animation_duration_short).toLong()
    animator.start()
}

/**
 * Animates the text of this [TextView] to the given value.
 *
 * @param toValue The dimension resource, to which the text's size will be animated (end value).
 */
fun TextView.animateTextSizeChange(@DimenRes toValue: Int) {
    val animator = ValueAnimator.ofFloat(this.textSize, resources.getDimension(toValue))
    animator.addUpdateListener { setTextSize(TypedValue.COMPLEX_UNIT_PX, it.animatedValue as Float) }
    animator.duration = resources.getInteger(R.integer.animation_duration_short).toLong()
    animator.start()
}

/**
 * Animates the height of this view from the given value to zero
 *
 * @param toValue The dimension resource, to which the height will be animated (end value)
 * @param duration An optional duration that can be supplied as an integer resource. The default
 * value is [R.integer.animation_duration] or 300ms
 */
fun View.animateToHeight(@DimenRes toValue: Int, @IntegerRes duration: Int = R.integer.animation_duration): Animator {
    this.layoutParams.height = 0
    this.requestLayout()

    val animator = ValueAnimator.ofFloat(0f, resources.getDimension(toValue))
    animator.interpolator = DecelerateInterpolator()
    animator.addUpdateListener {
        this.layoutParams.height = (it.animatedValue as Float).toInt()
        this.layoutParams = layoutParams
    }

    animator.duration = resources.getInteger(duration).toLong()

    return animator
}

/**
 * Animates the height of this view from the given value to zero
 *
 * @param fromValue The dimension resource, from which the height will be animated (start value)
 * @param duration An optional duration that can be supplied as an integer resource. The default
 * value is [R.integer.animation_duration] or 300ms
 */
fun View.animateFromHeight(@DimenRes fromValue: Int, @IntegerRes duration: Int = R.integer.animation_duration): Animator {
    val animator = ValueAnimator.ofFloat(resources.getDimension(fromValue), 0f)

    animator.interpolator = DecelerateInterpolator()
    animator.addUpdateListener {
        layoutParams.height = (it.animatedValue as Float).toInt()
        this.layoutParams = layoutParams
    }

    animator.duration = resources.getInteger(duration).toLong()

    return animator
}

/**
 * Animates the translation-Y of a view from the given value to its original position (0).
 *
 * @param fromValue The dimension resource, from which the view's translation-Y will be animated
 */
fun View.animateFromTranslationY(@DimenRes fromValue: Int) {
    this.translationY = -resources.getDimension(fromValue)

    val view = this

    this.animate()
            .setDuration(resources.getInteger(R.integer.animation_duration).toLong())
            .translationY(0F)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    view.visibility = View.VISIBLE
                }
            })
            .start()
}

/**
 * Animates the translationY of a view to the given value from its original position
 *
 * @param toValue The dimension value, to which the view's translation-Y will be animated
 */
fun View.animateToTranslationY(@DimenRes toValue: Int) {
    this.translationY = 0f

    val view = this

    this.animate()
            .setDuration(resources.getInteger(R.integer.animation_duration).toLong())
            .translationY(-resources.getDimension(toValue))
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    view.visibility = View.GONE
                }
            })
            .start()
}


/**
 * Animates the top padding of a view to the given value
 *
 * @param toValue The dimension value, to which the view's top padding will be animated
 */
fun View.animateTopPadding(@DimenRes toValue: Int) {
    val animator = ValueAnimator.ofInt(this.paddingTop, resources.getDimension(toValue).toInt())
    animator.addUpdateListener { this.setPaddingTop(it.animatedValue as Int) }
    animator.duration = resources.getInteger(R.integer.animation_duration_short).toLong()
    animator.start()
}