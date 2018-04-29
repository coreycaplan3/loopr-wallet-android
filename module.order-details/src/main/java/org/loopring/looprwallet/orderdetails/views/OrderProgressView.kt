package org.loopring.looprwallet.orderdetails.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.order_progress.view.*
import org.loopring.looprwallet.orderdetails.R


/**
 * Created by Corey Caplan on 4/7/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class OrderProgressView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    var progress: Int = 0
        @SuppressLint("SetTextI18n")
        set(value) {
            field = value

            openOrderProgressLabel.text = "$value%"
            openOrderProgress.progress = value
        }

    init {
        inflate(context, R.layout.order_progress, this)
    }

}