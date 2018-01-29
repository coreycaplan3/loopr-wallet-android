package com.caplaninnovations.looprwallet.utilities

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Base64
import android.util.Base64.NO_WRAP



/**
 *  Created by Corey on 1/29/2018.
 *  Project: loopr-wallet-android
 * <p></p>
 *  Purpose of Class:
 */
object SharedPreferenceUtility {

    private const val KEY_PREFERENCE_NAME = "_LooprWallet"

    fun get(context: Context, key: String): String? {
        val sharedPreferences = getPreference(context)

        return sharedPreferences.getString(key, null) ?: return null
    }

    fun put(context: Context, key: String, value: String) {
        getPreference(context).edit()
                .putString(key, value)
                .apply()
    }

    // MARK - Private Methods

    private fun getPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

}