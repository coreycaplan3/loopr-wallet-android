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

    private object Tags {

        private const val TAG_SECURITY_LOCKOUT_TIME_MILLIS = "_SECURITY_LOCKOUT_TIME_MILLIS"

        private const val TAG_THEME = "_THEME"

    }

    /*
     * MARK - DEFAULT VALUES
     */
    private object DefaultValues {

        private const val lockoutTimeMillis = 1000 * 60

    }

    /*
     * MARK - Public Methods
     */

    /**
     * @return The time (in millis) from which the application will be locked after leaving the
     * foreground.
     */
    fun getLockoutTime(): Long {
        return SharedPreferenceUtility.getLong(context, tagSecurityLockoutTimeMillis, lockoutTimeMillis)
    }

    @LooprTheme
    fun getTheme(): String {
        SharedPreferenceUtility.getString(context, )
    }

    fun saveTheme(@LooprTheme theme: String) {
        SharedPreferenceUtility.put(context, theme, )
    }

}