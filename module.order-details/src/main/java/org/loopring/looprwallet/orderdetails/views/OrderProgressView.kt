package org.loopring.looprwallet.orderdetails.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.AttributeSet
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.order_progress.view.*
import org.loopring.looprwallet.core.extensions.getResourceIdFromAttrId
import org.loopring.looprwallet.core.utilities.ApplicationUtility.col
import org.loopring.looprwallet.core.utilities.ApplicationUtility.drawable
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

            val colorAccent = col(context.theme.getResourceIdFromAttrId(R.attr.colorAccent))
            openOrderProgressLabel.text = "$value%"
            if (value == 0) {
                openOrderProgress.progressDrawable = drawable(R.drawable.progress_circle_none, context)
                openOrderProgress.progress = 100
            } else {
                openOrderProgress.progressDrawable = drawable(R.drawable.progress_circle, context)
                DrawableCompat.setTint(openOrderProgress.progressDrawable, colorAccent)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    openOrderProgress.setProgress(value, true)
                } else {
                    openOrderProgress.progress = value
                }
            }
        }

    init {
        inflate(context, R.layout.order_progress, this)
    }

}