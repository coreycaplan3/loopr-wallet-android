package org.loopring.looprwallet.core.views

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by Corey on 4/25/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class: A ViewPager that disables swiping between pages
 *
 */
class LockedViewPager @JvmOverloads constructor (context: Context, attrs: AttributeSet? = null) : ViewPager(context, attrs) {

    override fun onInterceptHoverEvent(event: MotionEvent?): Boolean {
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

}