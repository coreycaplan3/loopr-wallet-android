package org.loopring.looprwallet.core.utilities

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.graphics.drawable.DrawableCompat
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.extensions.getResourceIdFromAttrId
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.extensions.logw
import org.loopring.looprwallet.core.utilities.ApplicationUtility.drawable

/**
 *  Created by Corey on 4/24/2018.
 *
 *  Project: loopr-android
 *
 *  Purpose of Class:
 *
 */
object ImageUtility {

    /**
     * Dynamically gets the image from the provided [ticker] from the drawable resources
     *
     * @param ticker The ticker whose image will be retrieved
     * @param context The *activity* context to retrieve the default drawable's tint if the
     * provided resource is not found
     * @return The drawable of the corresponding ticker or a *default* one if it wasn't found
     */
    fun getImageFromTicker(ticker: String, context: Context): Drawable {
        return try {
            val fieldName = "token_${ticker.toLowerCase().trim()}"
            val drawableClass = R.drawable::class.java
            drawable(drawableClass.getField(fieldName).getInt(null), context)
        } catch (e: Throwable) {
            logw("Could not load image for $ticker")
            drawable(R.drawable.ic_help_outline_white_24dp, context).apply {
                val textColor = context.theme.getResourceIdFromAttrId(android.R.attr.textColorPrimary)
                DrawableCompat.setTint(this, ApplicationUtility.col(textColor))
            }
        }
    }

}