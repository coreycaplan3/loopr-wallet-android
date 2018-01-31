package com.caplaninnovations.looprwallet.models.android.settings

import android.content.Context
import android.content.SharedPreferences
import com.caplaninnovations.looprwallet.utilities.fromJson
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONTokener

/**
 *  Created by Corey on 1/29/2018.
 *  Project: loopr-wallet-android
 * <p></p>
 *  Purpose of Class:
 */
abstract class LooprSettingsManager(private val context: Context) {

    /*
     * MARK - TAGS
     */

    internal companion object Keys {

        const val KEY_SECURITY_LOCKOUT_TIME_MILLIS = "_SECURITY_LOCKOUT_TIME_MILLIS"

        const val KEY_THEME = "_THEME"

        private const val KEY_PREFERENCE_NAME = "_LooprWallet"
    }

    //
    // GETS
    //

    fun getLong(key: String, defaultValue: Long): Long {
        val sharedPreferences = getSharedPreferences(context)

        return sharedPreferences.getLong(key, defaultValue)
    }

    fun getString(key: String): String? {
        val sharedPreferences = getSharedPreferences(context)

        return sharedPreferences.getString(key, null) ?: return null
    }

    fun getStringArray(key: String): Array<String>? {
        val jsonArray = getSharedPreferences(context).getString(key, null)

        return jsonArray?.let { Gson().fromJson(it) }
    }

    //
    // PUTS
    //

    fun putLong(key: String, value: Long) {
        getSharedPreferences(context)
                .edit()
                .putLong(key, value)
                .apply()
    }

    fun putString(key: String, value: String?) {
        getSharedPreferences(context)
                .edit()
                .putString(key, value)
                .apply()
    }

    fun putStringArray(key: String, value: Array<String>) {
        getSharedPreferences(context)
                .edit()
                .putString(key, Gson().toJson(value))
                .apply()
    }


    // MARK - Private Methods

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

}