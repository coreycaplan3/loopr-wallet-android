package com.caplaninnovations.looprwallet.utilities

import android.util.Log
import kotlin.reflect.KClass

/**
 * Created by Corey on 1/18/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
fun <T : Any> T.logv(message: String, throwable: Throwable? = null) {
    Log.v(this::class.java.simpleName, message, throwable)
}

fun <T : Any> T.logd(message: String, throwable: Throwable? = null) {
    Log.d(this::class.java.simpleName, message, throwable)
}

fun <T : Any> T.logi(message: String, throwable: Throwable? = null) {
    Log.i(this::class.java.simpleName, message, throwable)
}

fun <T : Any> T.logw(message: String, throwable: Throwable? = null) {
    Log.w(this::class.java.simpleName, message, throwable)
}

fun <T : Any> T.loge(message: String, throwable: Throwable? = null) {
    Log.e(this::class.java.simpleName, message, throwable)
}