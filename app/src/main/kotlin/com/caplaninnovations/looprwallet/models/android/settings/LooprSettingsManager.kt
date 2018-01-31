package com.caplaninnovations.looprwallet.models.android.settings

import android.content.Context
import android.support.annotation.StyleRes
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.utilities.SharedPreferenceUtility

/**
 *  Created by Corey on 1/29/2018.
 *  Project: loopr-wallet-android
 * <p></p>
 *  Purpose of Class:
 */
class LooprSettingsManager(private val context: Context) {

    /*
     * MARK - TAGS
     */

    private object Keys {

        const val SECURITY_LOCKOUT_TIME_MILLIS = "_SECURITY_LOCKOUT_TIME_MILLIS"

        const val THEME = "_THEME"

    }

    /*
     * MARK - Public Methods
     */

    /**
     * @return The time (in millis) from which the application will be locked after leaving the
     * foreground.
     */
    fun getLockoutTime(): Long {
        return SharedPreferenceUtility.getLong(context, Keys.SECURITY_LOCKOUT_TIME_MILLIS, LooprLockoutSettings.DEFAULT_LOCKOUT_TIME_MILLIS)
    }

    /**
     * @param lockoutTime The time (in millis) from which the application will be locked after leaving the
     * foreground.
     */
    fun putLockoutTime(lockoutTime: Long) {
        SharedPreferenceUtility.putLong(context, Keys.SECURITY_LOCKOUT_TIME_MILLIS, lockoutTime)
    }

    /**
     * The theme for the application
     */
    @StyleRes
    fun getCurrentTheme(): Int {
        val theme = SharedPreferenceUtility.getString(context, Keys.THEME) ?: LooprTheme.lightTheme
        return when(theme) {
            LooprTheme.lightTheme -> R.style.AppTheme_Light
            LooprTheme.darkTheme -> R.style.AppTheme_Dark
            else -> throw IllegalStateException("Invalid theme, found: $theme")
        }
    }

    fun saveTheme(@LooprTheme.Name theme: String) {
        SharedPreferenceUtility.putString(context, Keys.THEME, theme)
    }

}