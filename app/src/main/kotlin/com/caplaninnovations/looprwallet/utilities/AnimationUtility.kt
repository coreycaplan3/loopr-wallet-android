package com.caplaninnovations.looprwallet.utilities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.support.annotation.DimenRes
import android.view.View
import android.widget.TextView
import com.caplaninnovations.looprwallet.R

/**
 * Created by Corey on 1/18/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */

/**
 * Animates the alpha to the given value. Must be between 0 and 1.
 */
fun View.animateAlpha(toValue: Float) {
    val animator = ValueAnimator.ofFloat(this.alpha, toValue)
    animator.addUpdateListener { this.alpha = it.animatedValue as Float }
    animator.duration = resources.getInteger(R.integer.animation_duration_short).toLong()
    animator.start()
}

/**
 * Animates the text view text to the given value
 */
fun TextView.animateTextSizeChange(@DimenRes toValue: Int) {
    val animator = ValueAnimator.ofFloat(this.textSize, resources.getDimension(toValue))
    animator.addUpdateListener { this.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, it.animatedValue as Float) }
    animator.duration = resources.getInteger(R.integer.animation_duration_short).toLong()
    animator.start()
}

/**
 * Animates the translationY of a view from the given value to its original position
 */
fun View.animateHeight(@DimenRes fromValue: Int, @DimenRes toValue: Int) {
    // TODO
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
 * Animates the translationY of a view from the given value to its original position
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
 */
fun View.animateTopPadding(@DimenRes toValue: Int) {
    val animator = ValueAnimator.ofInt(this.paddingTop, resources.getDimension(toValue).toInt())
    animator.addUpdateListener { this.setPaddingTop(it.animatedValue as Int) }
    animator.duration = resources.getInteger(R.integer.animation_duration_short).toLong()
    animator.start()
}