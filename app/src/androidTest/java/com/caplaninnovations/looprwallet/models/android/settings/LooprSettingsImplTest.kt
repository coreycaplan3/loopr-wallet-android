package com.caplaninnovations.looprwallet.models.android.settings

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Corey Caplan on 2/3/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
class LooprSettingsImplTest : LooprSettings {

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