package org.loopring.looprwallet.core.application

import android.app.Application
import android.content.Context
import org.loopring.looprwallet.core.activities.BaseActivity

/**
 * Created by Corey Caplan on 4/2/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
object LooprWalletCoreApp {

    /**
     * The class representing the *MainActivity* for the app
     */
    lateinit var mainClass: Class<out BaseActivity>

    lateinit var signInClass: Class<out BaseActivity>

    lateinit var application: Application

    val context: Context
        get() = application
}