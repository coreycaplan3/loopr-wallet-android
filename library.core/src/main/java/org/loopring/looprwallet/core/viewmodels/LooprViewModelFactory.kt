package org.loopring.looprwallet.core.viewmodels

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.core.models.wallet.LooprWallet

/**
 * Created by Corey Caplan on 3/14/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A [ViewModelProvider.Factory] instance that creates [ViewModel] objects that
 * require a [LooprWallet] instance in their constructor
 *
 */
open class LooprViewModelFactory protected constructor(private val currentWallet: LooprWallet)
    : ViewModelProvider.Factory {

    companion object {

        inline fun <reified T : ViewModel> get(fragment: Fragment): T {
            return ViewModelProviders.of(fragment).get(T::class.java)
        }

        inline fun <reified T : ViewModel> get(fragment: Fragment, currentWallet: LooprWallet): T {
            return ViewModelProviders.of(fragment, LooprViewModelFactory(currentWallet))
                    .get(T::class.java)
        }

    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        val constructor = modelClass.getConstructor(LooprWallet::class.java)
        return try {
            constructor.newInstance(currentWallet)
        } catch (e: Exception) {
            loge("Exception: ", e.cause ?: e)
            throw RuntimeException(e)
        }
    }

}