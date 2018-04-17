package org.loopring.looprwallet.core.extensions

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import org.loopring.looprwallet.core.models.android.architecture.RealmListLiveData
import org.loopring.looprwallet.core.models.android.architecture.RealmObjectLiveData
import io.realm.RealmModel
import io.realm.RealmResults

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
 * After the [onChange] function is invoked, the value in this [MutableLiveData] is set to *null*.
 */
fun <T> LiveData<T>.observeForDoubleSpend(owner: LifecycleOwner, onChange: (T) -> Unit) {
    if (this is MutableLiveData) {
        this.observe(owner, Observer {
            it?.let {
                onChange.invoke(it)
                this.value = null
            }
        })
    }
}

/**
 * Returns this realm result set as a native [LiveData] object for usage with architecture
 * components.
 */
fun <T : RealmModel> RealmResults<T>.asLiveData() = RealmListLiveData(this)

/**
 * Returns this realm result set as a native [LiveData] object for usage with architecture
 * components.
 */
fun <T : RealmModel> T.asLiveData() = RealmObjectLiveData(this)