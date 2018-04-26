package org.loopring.looprwallet.core.models.android.architecture

import android.arch.lifecycle.LiveData
import io.realm.RealmChangeListener
import io.realm.RealmModel
import io.realm.kotlin.addChangeListener
import io.realm.kotlin.removeChangeListener

/**
 * Created by Corey Caplan on 3/13/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
/**
 * Created by Corey Caplan on 3/13/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To use realm with [LiveData], to conform to Android's architecture
 * components. This class's only responsibility
 *
 * @param result The result from a query. Don't worry about it having data or not (from a query),
 * since a [RealmChangeListener] is registered and updates this [LiveData]'s value on the fly.
 */
class RealmObjectLiveData<T : RealmModel>(private val result: T) : LiveData<T>() {

    init {
        value = result
    }

    private val listener = RealmChangeListener<T> { results -> postValue(results) }

    override fun onActive() {
        result.addChangeListener(listener)
    }

    override fun onInactive() {
        result.removeChangeListener(listener)
    }

}