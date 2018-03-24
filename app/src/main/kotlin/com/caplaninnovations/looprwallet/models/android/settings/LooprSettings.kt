package com.caplaninnovations.looprwallet.models.android.settings

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.caplaninnovations.looprwallet.BuildConfig
import com.caplaninnovations.looprwallet.extensions.logd
import com.caplaninnovations.looprwallet.utilities.BuildUtility

/**
 * Created by Corey on 3/24/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A settings instance that is safe to be used with Android's *PreferenceManager*.
 */
interface LooprSettings {

    companion object {

        private var looprSettings: LooprSettings? = null

        @Synchronized
        fun getInstance(context: Context): LooprSettings {
            if (looprSettings != null) {
                return looprSettings as LooprSettings
            }

            val flavor = BuildConfig.ENVIRONMENT
            looprSettings = when (flavor) {
                BuildUtility.FLAVOR_MOCKNET -> LooprSettingsDebugImpl()
                BuildUtility.FLAVOR_TESTNET, BuildUtility.FLAVOR_MAINNET -> LooprSettingsProductionImpl(context)
                else -> throw IllegalArgumentException("Invalid build type, found: $flavor")
            }

            logd("Initialized $flavor LooprSettings to ${looprSettings!!::class.java.simpleName}")

            return looprSettings as LooprSecureSettings
        }

    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean

    fun getByteArray(key: String): ByteArray?

    fun getInt(key: String, defaultValue: Int): Int

    fun getLong(key: String, defaultValue: Long): Long

    fun getDouble(key: String, defaultValue: Double): Double

    fun getString(key: String): String?

    fun getStringArray(key: String): Array<String>?

    fun putBoolean(key: String, value: Boolean?)

    fun putByteArray(key: String, value: ByteArray?)

    fun putInt(key: String, value: Int?)

    fun putDouble(key: String, value: Double?)

    fun putLong(key: String, value: Long?)

    fun putString(key: String, value: String?)

    fun putStringArray(key: String, value: Array<String>?)

    @Suppress("unused")
    private class LooprSettingsDebugImpl : LooprSecureSettings {

        private val map = HashMap<String, Any>()

        fun clear() {
            map.clear()
        }

        override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
            return map[key] as? Boolean ?: defaultValue
        }

        override fun getByteArray(key: String): ByteArray? {
            return map[key] as? ByteArray
        }

        override fun getInt(key: String, defaultValue: Int): Int {
            return map[key] as? Int ?: defaultValue
        }

        override fun getDouble(key: String, defaultValue: Double): Double {
            return map[key] as? Double ?: defaultValue
        }

        override fun getLong(key: String, defaultValue: Long): Long {
            return map[key] as? Long ?: defaultValue
        }

        override fun getString(key: String): String? {
            return map[key] as? String
        }

        @Suppress("UNCHECKED_CAST")
        override fun getStringArray(key: String): Array<String>? {
            return map[key] as? Array<String>
        }

        override fun putBoolean(key: String, value: Boolean?) {
            handlePut(key, value)
        }

        override fun putByteArray(key: String, value: ByteArray?) {
            handlePut(key, value)
        }

        override fun putInt(key: String, value: Int?) {
            handlePut(key, value)
        }

        override fun putDouble(key: String, value: Double?) {
            handlePut(key, value)
        }

        override fun putLong(key: String, value: Long?) {
            handlePut(key, value)
        }

        override fun putString(key: String, value: String?) {
            handlePut(key, value)
        }

        override fun putStringArray(key: String, value: Array<String>?) {
            handlePut(key, value)
        }

        private fun handlePut(key: String, value: Any?) {
            if (value != null) {
                map[key] = value
            } else {
                map.remove(key)
            }
        }
    }

    // END DEBUG IMPL

    private class LooprSettingsProductionImpl(private val context: Context) : LooprSecureSettings {

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

            return sharedPreferences.getString(key, null)
        }

        override fun getStringArray(key: String): Array<String>? {
            return getSharedPreferences().getStringSet(key, null)?.toTypedArray()
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
                        .putString(key, value)
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
                    .putStringSet(key, value?.toSet())
                    .apply()
        }


        // MARK - Private Methods

        private fun getSharedPreferences(): SharedPreferences {
            // WARNING!!! DO NOT CHANGE THIS. THE ANDROID PREFERENCE FRAMEWORK WRITES TO AND READS
            // FROM THIS SHARED PREFERENCE INSTANCE. SOME THINGS, LIKE KEYS AND WALLET NAMES, ARE
            // ENCRYPTED AND SAVED WITHOUT THE ANDROID FRAMEWORK EVER TOUCHING IT. THUS, VALUES FOR
            // IMPORTANT DATA REMAINS ENCRYPTED AND UNTOUCHED BY THE SETTINGS ACTIVITY!
            return PreferenceManager.getDefaultSharedPreferences(context)
        }
    }

    // END PRODUCTION IMPL

}