package com.caplaninnovations.looprwallet.models.android.settings

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.caplaninnovations.looprwallet.BuildConfig
import com.caplaninnovations.looprwallet.utilities.fromJson
import com.caplaninnovations.looprwallet.utilities.logd
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
interface LooprSettings {

    companion object {

        private var looprSettings: LooprSettings? = null

        fun getInstance(context: Context): LooprSettings {
            if (looprSettings != null) {
                return looprSettings as LooprSettings
            }

            val buildType = BuildConfig.BUILD_TYPE
            looprSettings = when (buildType) {
                "debug" -> LooprSettingsDebugImpl()
                "staging", "release" -> LooprSettingsProductionImpl(context)
                else -> throw IllegalArgumentException("Invalid build type, found: $buildType")
            }

            logd("Initialized $buildType LooprSettings to ${looprSettings!!::class.java.simpleName}")

            return looprSettings as LooprSettings
        }

    }

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