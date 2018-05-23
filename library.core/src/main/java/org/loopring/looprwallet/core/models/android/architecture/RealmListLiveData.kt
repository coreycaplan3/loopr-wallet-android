package org.loopring.looprwallet.core.models.android.architecture

import android.arch.lifecycle.LiveData
import io.realm.*


/**
 * Created by Corey Caplan on 3/13/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To represent realm using [LiveData], to conform to Android's architecture
 * components.
 *
 * @param results The results from a query. Don't worry about it having data or not (from a query),
 * since a [RealmChangeListener] is registered and updates this [LiveData]'s value on the fly.
 */
class RealmListLiveData<T : RealmModel>(private val results: OrderedRealmCollection<T>)
    : LiveData<OrderedRealmCollection<T>>() {

    override fun onActive() {
        when (results) {
            is RealmResults<*> -> results.addChangeListener { _: RealmResults<*>? -> value = results }
            is RealmList<*> -> results.addChangeListener { _: RealmList<*>? -> value = results }
            else -> throw IllegalArgumentException("RealmCollection not supported: " + results.javaClass.simpleName)
        }
    }

    override fun onInactive() {
        when (results) {
            is RealmResults<*> -> results.removeAllChangeListeners()
            is RealmList<*> -> results.removeAllChangeListeners()
            else -> throw IllegalArgumentException("RealmCollection not supported: " + results.javaClass.simpleName)
        }

    }

}