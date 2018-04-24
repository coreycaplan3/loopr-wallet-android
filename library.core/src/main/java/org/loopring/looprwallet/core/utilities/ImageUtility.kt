package org.loopring.looprwallet.core.utilities

import android.support.annotation.DrawableRes
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.extensions.logi

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
     * @return The drawable resource of the corresponding ticker or *null* if it wasn't found
     */
    @DrawableRes
    fun getImageFromTicker(ticker: String): Int? {
        return try {
            R.drawable::class.java.getField(ticker.toLowerCase().trim()).getInt(null)
        } catch (e: Throwable) {
            logi("$ticker\'s image was not found!")
            null
        }
    }

}