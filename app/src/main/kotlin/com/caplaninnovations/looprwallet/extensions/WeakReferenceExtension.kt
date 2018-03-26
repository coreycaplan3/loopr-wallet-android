package com.caplaninnovations.looprwallet.extensions

import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

/**
 * Created by Corey Caplan on 3/26/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A delegate that supports have weak references.
 *
 * See [weakReference] for usage.
 */
class WeakRefHolder<T>(private var _value: WeakReference<T?>) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return _value.get()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        _value = WeakReference(value)
    }
}

/**
 * Use the *by* keyword to delegate weak references.
 *
 * Internally this creates a [WeakRefHolder] that will store the real [WeakReference]. Example is
 * as follows:
 *
 *     var weakContext: Context? by weakReference(null)
 *     …
 *     weakContext = strongContext
 *     …
 *     context = weakContext
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T> weakReference(value: T) = WeakRefHolder<T>(WeakReference(value))