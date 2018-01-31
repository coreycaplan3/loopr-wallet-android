package com.caplaninnovations.looprwallet.models.android.settings

import android.content.Context

/**
 *  Created by Corey on 1/30/2018.
 *  Project: loopr-wallet-android
 * <p></p>
 *  Purpose of Class:
 */
class LooprLockoutSettings(context: Context) : LooprSettingsManager(context) {

    companion object {

        const val DEFAULT_LOCKOUT_TIME_MILLIS: Long = 1000 * 60

    }

    /**
     * @return The time (in millis) from which the application will be locked after leaving the
     * foreground.
     */
    fun getLockoutTime(): Long {
        return getLong(Keys.KEY_SECURITY_LOCKOUT_TIME_MILLIS, DEFAULT_LOCKOUT_TIME_MILLIS)
    }

    /**
     * @param lockoutTime The time (in millis) from which the application will be locked after leaving the
     * foreground.
     */
    fun putLockoutTime(lockoutTime: Long) {
        putLong(KEY_SECURITY_LOCKOUT_TIME_MILLIS, lockoutTime)
    }

}