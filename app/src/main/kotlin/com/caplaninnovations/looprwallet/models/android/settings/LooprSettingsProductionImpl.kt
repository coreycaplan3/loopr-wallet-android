package com.caplaninnovations.looprwallet.models.android.settings

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.caplaninnovations.looprwallet.utilities.fromJson
import com.google.gson.Gson
import io.realm.android.CipherClient

/**
 * Created by Corey on 3/5/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
class LooprSettingsProductionImpl(private val context: Context) : LooprSettings {

    companion object {

        const val KEY_SHARED_PREFERENCE_NAME = "_LooprWallet"
    }

    private val cipherClient: CipherClient = CipherClient(context)

    //
    // GETS
    //

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return getString(key)?.toBoolean() ?: defaultValue
    }

    override fun getByteArray(key: String): ByteArray? {
        return getString(key)?.toByteArray(Charsets.UTF_8)
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return getString(key)?.toInt() ?: defaultValue
    }

    override fun getDouble(key: String, defaultValue: Double): Double {
        return getString(key)?.toDouble() ?: defaultValue
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return getString(key)?.toLong() ?: defaultValue
    }

    override fun getString(key: String): String? {
        val sharedPreferences = getSharedPreferences()

        return sharedPreferences.getString(key, null)?.let { cipherClient.decrypt(it) }
    }

    override fun getStringArray(key: String): Array<String>? {
        val jsonArray = getString(key)

        return jsonArray?.let { Gson().fromJson(it) }
    }

    //
    // PUTS
    //

    override fun putBoolean(key: String, value: Boolean?) {
        putString(key, value?.toString())
    }

    override fun putByteArray(key: String, value: ByteArray?) {
        putString(key, value?.let { String(it, Charsets.UTF_8) })
    }

    override fun putInt(key: String, value: Int?) {
        putString(key, value?.toString())
    }

    override fun putDouble(key: String, value: Double?) {
        putString(key, value?.toString())
    }

    override fun putLong(key: String, value: Long?) {
        putString(key, value?.toString())
    }

    override fun putString(key: String, value: String?) {
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

    override fun putStringArray(key: String, value: Array<String>?) {
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