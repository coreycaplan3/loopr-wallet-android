package com.caplaninnovations.looprwallet.models.android.observers

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer

/**
 * Created by Corey on 3/2/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */

/**
 * An extension of observer whose responsibility is to reset the observer's value
 * after receiving the result so we don't do the same thing twice. This "double-action" could occur
 * if we rotate the device, since it'll recreate the activity and resend the same values in this
 * observer a second time.
 *
 * After the [onChange] function is invoked, the value in this [MutableLiveData] is set to *null*
 */
fun <T> MutableLiveData<T>.observeForDoubleSpend(owner: LifecycleOwner, onChange: (T) -> Unit) {
    this.observe(owner, Observer {
        it?.let {
            onChange.invoke(it)
            value = null
        }
    })
}