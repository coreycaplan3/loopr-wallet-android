package com.caplaninnovations.looprwallet.utilities

import android.content.Context
import android.content.SharedPreferences


/**
 *  Created by Corey on 1/29/2018.
 *  Project: loopr-wallet-android
 * <p></p>
 *  Purpose of Class:
 */
object SharedPreferenceUtility {

    private const val KEY_PREFERENCE_NAME = "_LooprWallet"

    //
    // GETS
    //

    fun getLong(context: Context, key: String, defaultValue: Long): Long {
        val sharedPreferences = getSharedPreferences(context)

        return sharedPreferences.getLong(key, defaultValue)
    }

    fun getString(context: Context, key: String): String? {
        val sharedPreferences = getSharedPreferences(context)

        return sharedPreferences.getString(key, null) ?: return null
    }

    //
    // PUTS
    //

    fun putLong(context: Context, key: String, value: Long) {
        getSharedPreferences(context)
                .edit()
                .putLong(key, value)
                .apply()
    }

    fun putString(context: Context, key: String, value: String) {
        getSharedPreferences(context)
                .edit()
                .putString(key, value)
                .apply()
    }

    // MARK - Private Methods

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

}