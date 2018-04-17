package org.loopring.looprwallet.core.extensions

import android.animation.*
import android.support.annotation.DimenRes
import android.support.annotation.IntegerRes
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import org.loopring.looprwallet.core.R

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
fun View.animateToAlpha(toValue: Float, @IntegerRes duration: Int = R.integer.animation_duration) {
    if (toValue < 0 || toValue > 1) {
        throw IllegalArgumentException("The inputted toValue must be between [0, 1]. Found: $toValue")
    }

    val animator = ValueAnimator.ofFloat(this.alpha, toValue)
    animator.addUpdateListener { this.alpha = it.animatedValue as Float }
    animator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            if (toValue != 0F) {
                visibility = View.VISIBLE
            }
        }

        override fun onAnimationEnd(animation: Animator?) {
            if (toValue == 0F) {
                visibility = View.GONE
            }
        }
    })

    animator.duration = resources.getInteger(duration).toLong()
    animator.start()
}

/**
 * Animates the text of this [TextView] to the given value.
 *
 * @param toValue The dimension resource, to which the text's size will be animated (end value).
 */
fun TextView.animateTextSizeChange(@DimenRes toValue: Int, @IntegerRes duration: Int = R.integer.animation_duration) {
    val endSize = resources.getDimension(toValue)

    val view = this
    val animator = ValueAnimator.ofFloat(this.textSize, endSize)
    animator.addUpdateListener { setTextSize(TypedValue.COMPLEX_UNIT_PX, it.animatedValue as Float) }
    animator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            if (endSize != 0F) {
                view.visibility = View.VISIBLE
            }
        }

        override fun onAnimationEnd(animation: Animator?) {
            if (endSize == 0F) {
                view.visibility = View.GONE
            }
        }
    })
    animator.duration = resources.getInteger(duration).toLong()
    animator.start()
}

fun View.animateScaleX(toValue: Float, @IntegerRes duration: Int = R.integer.animation_duration) {
    pivotX = (this.measuredWidth / 2).toFloat()

    val animator = ObjectAnimator.ofFloat(this, View.SCALE_X, toValue)

    animator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            if (toValue != 0F) {
                visibility = View.VISIBLE
            }
        }

        override fun onAnimationEnd(animation: Animator?) {
            if (toValue == 0F) {
                visibility = View.GONE
            }
        }
    })

    animator.duration = resources.getInteger(duration).toLong()

    animator.start()
}

/**
 * Animates the scale of both x and y values of the view
 */
fun View.animateScaleBoth(toValue: Float, @IntegerRes duration: Int = R.integer.animation_duration): Animator {
    if (this.width != 0) {
        pivotX = (this.width / 2).toFloat()
    }
    if (this.height != 0) {
        pivotY = (this.height / 2).toFloat()
    }

    val actualDuration = resources.getInteger(duration).toLong()

    val xScale = ObjectAnimator.ofFloat(this, View.SCALE_X, toValue)
            .setDuration(actualDuration)

    val yScale = ObjectAnimator.ofFloat(this, View.SCALE_Y, toValue)
            .setDuration(actualDuration)

    val animator = AnimatorSet()
    animator.interpolator = LinearOutSlowInInterpolator()

    animator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            if (toValue != 0F) {
                visibility = View.VISIBLE
            }
        }

        override fun onAnimationEnd(animation: Animator?) {
            if (toValue == 0F) {
                visibility = View.GONE
            }
        }
    })

    animator.playTogether(xScale, yScale)
    animator.duration = actualDuration

    return animator
}

/**
 * Animates the scale of both x and y values of the view
 */
fun View.animateScaleBothAndStart(toValue: Float, @IntegerRes duration: Int = R.integer.animation_duration) {
    this.animateScaleBoth(toValue, duration).start()
}

/**
 * Animates the height of this view to the given value
 *
 * @param toValue The dimension resource, to which the height will be animated (end value)
 * @param duration An optional duration that can be supplied as an integer resource. The default
 * value is [R.integer.animation_duration] or 300ms
 */
fun View.animateToHeight(@DimenRes toValue: Int, @IntegerRes duration: Int = R.integer.animation_duration): Animator {
    val animator = ValueAnimator.ofFloat(measuredHeight.toFloat(), resources.getDimension(toValue))
    animator.interpolator = LinearOutSlowInInterpolator()
    animator.addUpdateListener {
        this.layoutParams.height = (it.animatedValue as Float).toInt()
        this.layoutParams = layoutParams
    }

    animator.duration = resources.getInteger(duration).toLong()

    return animator
}

/**
 * Animates the width of this view to the given value
 *
 * @param toValue The dimension resource, to which the width will be animated (end value)
 * @param duration An optional duration that can be supplied as an integer resource. The default
 * value is [R.integer.animation_duration] or 300ms
 */
fun View.animateToWidth(@DimenRes toValue: Int, @IntegerRes duration: Int = R.integer.animation_duration): Animator {
    val animator = ValueAnimator.ofFloat(measuredWidth.toFloat(), resources.getDimension(toValue))
    animator.interpolator = LinearOutSlowInInterpolator()
    animator.addUpdateListener {
        this.layoutParams.width = (it.animatedValue as Float).toInt()
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
fun View.animateTopPadding(@DimenRes toValue: Int, @IntegerRes duration: Int = R.integer.animation_duration) {
    val animator = ValueAnimator.ofInt(this.paddingTop, resources.getDimension(toValue).toInt())
    animator.addUpdateListener { this.setPaddingTop(it.animatedValue as Int) }
    animator.duration = resources.getInteger(duration).toLong()
    animator.start()
}