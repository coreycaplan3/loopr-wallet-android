package org.loopring.looprwallet.core.utilities

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v4.graphics.drawable.DrawableCompat
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.extensions.getResourceIdFromAttrId
import org.loopring.looprwallet.core.utilities.ApplicationUtility.col
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
            drawable(R.drawable::class.java.getField(fieldName).getInt(null))
        } catch (e: Throwable) {
            val d = drawable(R.drawable.ic_help_outline_white_24dp)
            val tintColor = context.theme.getResourceIdFromAttrId(android.R.attr.textColor)
            DrawableCompat.setTint(d, col(tintColor))
            d
        }
    }

}