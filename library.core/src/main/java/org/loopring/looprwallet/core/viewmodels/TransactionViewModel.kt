package org.loopring.looprwallet.core.viewmodels

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

    protected val mIsTransactionRunning = MutableLiveData<Boolean>()
    protected val mResult = MutableLiveData<T>()
    protected val mError = MutableLiveData<Throwable>()

    /**
     * A live data object for checking whether or not a a transaction is running.
     */
    val isTransactionRunning: LiveData<Boolean> = mIsTransactionRunning

    /**
     * A live data object that stores the result being processed by this [ViewModel].
     */
    val result: LiveData<T> = mResult

    /**
     * An error that may or may not have occurred. Should be used in conjunction with
     * *LiveData.observeForDoubleSpend* so you don't display the same error *twice* to a user.
     */
    val error: LiveData<Throwable> = mError

}