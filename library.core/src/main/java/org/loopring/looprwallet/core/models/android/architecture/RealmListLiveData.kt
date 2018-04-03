package org.loopring.looprwallet.core.models.android.architecture

import android.arch.lifecycle.LiveData
import io.realm.RealmChangeListener
import io.realm.RealmModel
import io.realm.RealmResults


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
class RealmListLiveData<T : RealmModel>(private val results: RealmResults<T>)
    : LiveData<RealmResults<T>>() {

    private val listener = RealmChangeListener<RealmResults<T>> { results -> value = results }

    override fun onActive() {
        results.addChangeListener(listener)
    }

    override fun onInactive() {
        results.removeChangeListener(listener)
    }

}