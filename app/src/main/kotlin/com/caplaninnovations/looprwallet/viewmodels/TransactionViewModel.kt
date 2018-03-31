package com.caplaninnovations.looprwallet.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

/**
 * Created by Corey Caplan on 3/10/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A [ViewModel] used for running basic transactions. This could be a network
 * request or
 *
 */
abstract class TransactionViewModel<T> : ViewModel() {

    /*
     * These are kept private to not directly expose the type of LiveData objects as
     * MutableLiveData. This is done because we don't want external classes modifying their values.
     */

    protected val isTransactionRunning = MutableLiveData<Boolean>()
    protected val result = MutableLiveData<T>()
    protected val error = MutableLiveData<Throwable>()

    /**
     * A live data object for checking whether or not a a transaction is running.
     */
    fun isTransactionRunning(): LiveData<Boolean> {
        return isTransactionRunning
    }

    /**
     * A live data object that stores the result being processed by this [ViewModel].
     */
    fun result(): LiveData<T> {
        return result
    }

    /**
     * An error that may or may not have occurred. Should be used in conjunction with
     * *LiveData.observeForDoubleSpend* so you don't display the same error *twice* to a user.
     */
    fun error(): LiveData<Throwable> {
        return error
    }

}