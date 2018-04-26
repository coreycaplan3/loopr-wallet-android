package org.loopring.looprwallet.core.models.android.architecture

import android.os.Handler
import android.os.HandlerThread
import kotlinx.coroutines.experimental.Delay
import kotlinx.coroutines.experimental.android.HandlerContext

/**
 * Created by Corey on 4/26/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */

/**
 * Dispatches execution onto an IO thread and provides native [delay][Delay.delay] support.
 */
val IO = HandlerThread("IO").let {
    it.start()
    return@let HandlerContext(Handler(it.looper), "IO")
}

/**
 * Dispatches execution onto a Network thread and provides native [delay][Delay.delay] support.
 */
val NET = HandlerThread("NET").let {
    it.start()
    return@let HandlerContext(Handler(it.looper), "NET")
}