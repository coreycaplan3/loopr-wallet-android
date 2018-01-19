package com.caplaninnovations.looprwallet.utilities

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
 * Animates the top padding of a view to the given value
 */
fun View.animateTopPadding(@DimenRes toValue: Int) {
    val animator = ValueAnimator.ofInt(this.paddingTop, resources.getDimension(toValue).toInt())
    animator.addUpdateListener { this.setPaddingTop(it.animatedValue as Int) }
    animator.duration = resources.getInteger(R.integer.animation_duration_short).toLong()
    animator.start()
}