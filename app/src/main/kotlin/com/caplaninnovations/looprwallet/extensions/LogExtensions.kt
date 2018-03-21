package com.caplaninnovations.looprwallet.extensions

import android.util.Log

/**
 * Created by Corey on 1/18/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class: To create utility methods for commonly-done things, to streamline code and
 * make it more readable
 *
 */

/**
 * Logs a message on a *VERBOSE* level
 */
inline fun <reified T> T.logv(message: String, throwable: Throwable? = null) {
    Log.v(T::class.java.simpleName, message, throwable)
}

/**
 * Logs a message on a *DEBUG* level
 */
inline fun <reified T> T.logd(message: String, throwable: Throwable? = null) {
    Log.d(T::class.java.simpleName, message, throwable)
}

/**
 * Logs a message on an *INFO* level
 */
inline fun <reified T> T.logi(message: String, throwable: Throwable? = null) {
    Log.i(T::class.java.simpleName, message, throwable)
}

/**
 * Logs a message on a *WARN* level
 */
inline fun <reified T> T.logw(message: String, throwable: Throwable? = null) {
    Log.w(T::class.java.simpleName, message, throwable)
}

/**
 * Logs a message on an *ERROR* level
 */
inline fun <reified T> T.loge(message: String, throwable: Throwable? = null) {
    Log.e(T::class.java.simpleName, message, throwable)
}