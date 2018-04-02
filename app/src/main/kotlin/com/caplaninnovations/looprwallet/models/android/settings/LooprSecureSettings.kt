package com.caplaninnovations.looprwallet.models.android.settings

import android.content.Context
import android.content.SharedPreferences
import com.caplaninnovations.looprwallet.BuildConfig
import org.loopring.looprwallet.core.extensions.fromJson
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_MAINNET
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_MOCKNET
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_TESTNET
import com.google.gson.Gson
import io.realm.android.CipherClient

/**
 * Created by Corey Caplan on 2/3/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To allow the user to save configurable settings easily and uniformly, while
 * minding their privacy and encrypting all information.
 *
 */
interface LooprSecureSettings : LooprSettings {

    companion object {

        private var looprSecureSettings: LooprSecureSettings? = null

        fun getInstance(context: Context): LooprSecureSettings {
            if (looprSecureSettings != null) {
                return looprSecureSettings as LooprSecureSettings
            }

            val flavor = BuildConfig.ENVIRONMENT
            looprSecureSettings = when (flavor) {
                FLAVOR_MOCKNET -> LooprSecureSettingsDebugImpl()
                FLAVOR_TESTNET, FLAVOR_MAINNET -> LooprSecureSettingsProductionImpl(context)
                else -> throw IllegalArgumentException("Invalid build type, found: $flavor")
            }

            logd("Initialized $flavor LooprSecureSettings to ${looprSecureSettings!!::class.java.simpleName}")

            return looprSecureSettings as LooprSecureSettings
        }

    }

    private class LooprSecureSettingsDebugImpl : LooprSecureSettings {

        private val map = HashMap<String, Any>()

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

    private class LooprSecureSettingsProductionImpl(private val context: Context) : LooprSecureSettings {

        private companion object {

            private const val SHARED_PREFERENCES_NAME = "_LooprWalletSecure"

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
            return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        }

    }

}