package com.caplaninnovations.looprwallet.utilities

import android.util.Log
import kotlin.reflect.KClass

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
fun <T : Any> T.logv(message: String, throwable: Throwable? = null) {
    Log.v(this::class.java.simpleName, message, throwable)
}

/**
 * Logs a message on a *DEBUG* level
 */
fun <T : Any> T.logd(message: String, throwable: Throwable? = null) {
    Log.d(this::class.java.simpleName, message, throwable)
}

/**
 * Logs a message on an *INFO* level
 */
fun <T : Any> T.logi(message: String, throwable: Throwable? = null) {
    Log.i(this::class.java.simpleName, message, throwable)
}

/**
 * Logs a message on a *WARN* level
 */
fun <T : Any> T.logw(message: String, throwable: Throwable? = null) {
    Log.w(this::class.java.simpleName, message, throwable)
}

/**
 * Logs a message on an *ERROR* level
 */
fun <T : Any> T.loge(message: String, throwable: Throwable? = null) {
    Log.e(this::class.java.simpleName, message, throwable)
}