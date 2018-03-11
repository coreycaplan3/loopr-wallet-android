package com.caplaninnovations.looprwallet.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

/**
 * Created by Corey Caplan on 3/10/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
abstract class AbstractTransactionViewModel<T> : ViewModel() {

    /*
     * These are kept private to not directly expose the type of LiveData objects as
     * MutableLiveData. This is done because we don't want external classes modifying their values.
     */

    protected val isTransactionRunning = MutableLiveData<Boolean>()
    protected val transactionResult = MutableLiveData<T>()
    protected val transactionError = MutableLiveData<Throwable>()

    fun isTransactionRunning(): LiveData<Boolean> {
        return isTransactionRunning
    }

    fun transactionResult(): LiveData<T> {
        return transactionResult
    }

    fun transactionError(): LiveData<Throwable> {
        return transactionError
    }

}