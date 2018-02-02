package com.caplaninnovations.looprwallet.models.android.settings

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.caplaninnovations.looprwallet.utilities.fromJson
import com.google.gson.Gson
import io.realm.android.CipherClient

/**
 *  Created by Corey on 1/29/2018.
 *  Project: loopr-wallet-android
 * <p></p>
 *  Purpose of Class:
 */
abstract class LooprSettingsManager(private val context: Context) {

    internal companion object Keys {

        const val KEY_THEME = "_THEME"

        const val KEY_SHARED_PREFERENCE_NAME = "_LooprWallet"
    }

    private val cipherClient: CipherClient = CipherClient(context)

    //
    // GETS
    //

    fun getByteArray(key: String): ByteArray? {
        return getString(key)?.let { Base64.decode(it, Base64.DEFAULT) }
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return getString(key)?.toLong() ?: defaultValue
    }

    fun getString(key: String): String? {
        val sharedPreferences = getSharedPreferences()

        return sharedPreferences.getString(key, null)?.let { cipherClient.decrypt(it) }
    }

    fun getStringArray(key: String): Array<String>? {
        val jsonArray = getString(key)

        return jsonArray?.let { Gson().fromJson(it) }
    }

    //
    // PUTS
    //

    fun putByteArray(key: String, value: ByteArray?) {
        putString(key, value?.let { Base64.encodeToString(it, Base64.DEFAULT) })
    }

    fun putLong(key: String, value: Long?) {
        putString(key, value.toString())
    }

    fun putString(key: String, value: String?) {
        if (value != null) {
            getSharedPreferences()
                    .edit()
                    .putString(key, cipherClient.encrypt(value))
                    .apply()
        } else {
            getSharedPreferences()
                    .edit()
                    .remove(key)
                    .apply()
        }
    }

    fun putStringArray(key: String, value: Array<String>?) {
        getSharedPreferences()
                .edit()
                .putString(key, value?.let { cipherClient.encrypt(Gson().toJson(it)) })
                .apply()
    }


    // MARK - Private Methods

    private fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(KEY_SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

}