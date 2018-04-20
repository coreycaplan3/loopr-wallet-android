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
class RealmListLiveData<T : RealmModel>(private val results: OrderedRealmCollection<T>) : LiveData<OrderedRealmCollection<T>>() {

    override fun onActive() {
        when (results) {
            is RealmResults<*> -> {
                (results as RealmResults<T>).addChangeListener { realmResults: RealmResults<T>? ->
                    value = realmResults
                }
            }
            is RealmList<*> -> {
                (results as RealmList<T>).addChangeListener { list: RealmList<T>? ->
                    value = list
                }
            }
            else -> throw IllegalArgumentException("RealmCollection not supported: " + results.javaClass.simpleName)
        }
    }

    override fun onInactive() {
        when (results) {
            is RealmResults<*> -> {
                val results = results as RealmResults<T>
                results.removeAllChangeListeners()
            }
            is RealmList<*> -> {
                val list = results as RealmList<T>
                list.removeAllChangeListeners()
            }
            else -> throw IllegalArgumentException("RealmCollection not supported: " + results.javaClass.simpleName)
        }

    }

}