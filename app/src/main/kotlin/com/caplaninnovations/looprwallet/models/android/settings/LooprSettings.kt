package com.caplaninnovations.looprwallet.models.android.settings

/**
 * Created by Corey Caplan on 2/3/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
interface LooprSettings {

    fun getBoolean(key: String, defaultValue: Boolean): Boolean

    fun getByteArray(key: String): ByteArray?

    fun getInt(key: String, defaultValue: Int): Int

    fun getDouble(key: String, defaultValue: Double): Double

    fun getLong(key: String, defaultValue: Long): Long

    fun getString(key: String): String?

    fun getStringArray(key: String): Array<String>?

    fun putBoolean(key: String, value: Boolean?)

    fun putByteArray(key: String, value: ByteArray?)

    fun putInt(key: String, value: Int?)

    fun putDouble(key: String, value: Double?)

    fun putLong(key: String, value: Long?)

    fun putString(key: String, value: String?)

    fun putStringArray(key: String, value: Array<String>?)

}