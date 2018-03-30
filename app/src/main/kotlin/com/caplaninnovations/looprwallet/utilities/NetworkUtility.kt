package com.caplaninnovations.looprwallet.utilities

import android.content.Context
import android.net.ConnectivityManager
import com.caplaninnovations.looprwallet.BuildConfig
import com.caplaninnovations.looprwallet.application.LooprWalletApp


/**
 * Created by Corey Caplan on 3/10/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
object NetworkUtility {

    var mockIsNetworkAvailable = true

    fun isNetworkAvailable() = when {
        BuildConfig.DEBUG -> mockIsNetworkAvailable
        else -> {
            val context = LooprWalletApp.context
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }

}