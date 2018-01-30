package com.caplaninnovations.looprwallet.models.android.settings

import android.content.Context
import com.caplaninnovations.looprwallet.utilities.SharedPreferenceUtility

/**
 *  Created by Corey on 1/29/2018.
 *  Project: loopr-wallet-android
 * <p></p>
 *  Purpose of Class:
 */
class LooprSettings(private val context: Context) {

    /*
     * MARK - TAGS
     */
    private val tagSecurityLockoutTimeMillis = "_SecurityLockoutTimeMillis"

    /*
     * MARK - DEFAULT VALUES
     */
    private val defaultLockoutTimeMillis: Long = 1000 * 60

    /*
     * MARK - Public Methods
     */

    /**
     * @return The time (in millis) from which the application will be locked after leaving the
     * foreground.
     */
    fun getLockoutTime(): Long {
        return SharedPreferenceUtility.getLong(context, tagSecurityLockoutTimeMillis, defaultLockoutTimeMillis)
    }

}