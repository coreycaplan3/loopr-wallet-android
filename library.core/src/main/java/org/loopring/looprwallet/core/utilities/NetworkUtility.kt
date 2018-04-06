package org.loopring.looprwallet.core.utilities

import android.content.Context
import android.net.ConnectivityManager
import org.loopring.looprwallet.core.BuildConfig
import org.loopring.looprwallet.core.application.CoreLooprWalletApp

/**
 * Created by Corey Caplan on 3/10/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
object NetworkUtility {

    val MOCK_SERVICE_CALL_DURATION = BuildConfig.DEFAULT_NETWORK_TIMEOUT - 200L

    var mockIsNetworkAvailable = true

    fun isNetworkAvailable() = when {
        BuildConfig.DEBUG -> mockIsNetworkAvailable
        else -> {
            val context = CoreLooprWalletApp.context
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }

}