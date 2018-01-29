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

    private const val PREF_NAME = "realm_key"
    private const val KEY = "iv_and_encrypted_key"

    fun save(context: Context, ivAndEncryptedKey: ByteArray) {
        getPreference(context).edit()
                .putString(KEY, encode(ivAndEncryptedKey))
                .apply()
    }

    fun load(context: Context): ByteArray? {
        val pref = getPreference(context)

        val ivAndEncryptedKey = pref.getString(KEY, null) ?: return null

        return decode(ivAndEncryptedKey)
    }

    private fun encode(data: ByteArray?): String? {
        return if (data == null) {
            null
        } else Base64.encodeToString(data, Base64.NO_WRAP)
    }

    private fun decode(encodedData: String?): ByteArray? {
        return if (encodedData == null) {
            null
        } else Base64.decode(encodedData, Base64.DEFAULT)
    }

    private fun getPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
}