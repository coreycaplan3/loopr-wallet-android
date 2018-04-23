package org.loopring.looprwallet.core.viewmodels

import android.arch.lifecycle.LiveData
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

/**
 * Created by Corey Caplan on 3/13/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A class responsible for establishing a basis for how subclasses should
 * GET streamed data, which needs frequent updates. Sub-classes should call [initializeData] to start
 * listening for changes in realm data.
 *
 * In this case, streamed data refers to a piece of singular data that is updated over periods of
 * time. For example, watching a single ticker.
 */
abstract class StreamingViewModel<T, U> : OfflineFirstViewModel<T, U>() {

    private val timer = Timer()

    final override fun onLiveDataInitialized(liveData: LiveData<T>) {
        timer.scheduleAtFixedRate(0L, waitTime) {
            refresh()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }

}