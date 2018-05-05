package org.loopring.looprwallet.core.views

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import android.view.MotionEvent


/**
 * Created by Corey on 5/5/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class LockableNestedScrollView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : NestedScrollView(context, attrs, defStyleAttr) {

    var isScrollEnabled = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return when (ev.action) {
            MotionEvent.ACTION_DOWN -> isScrollEnabled && super.onTouchEvent(ev)
            else -> super.onTouchEvent(ev)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return isScrollEnabled && super.onInterceptTouchEvent(ev)
    }

}