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

    object MainApplicationUtility {

        /**
         * The class representing the *MainActivity* for the app
         */
        lateinit var clazz: Class<out BaseActivity>

        /**
         * Sets up the main activity class and assigns it to [clazz] so it can be referenced
         * throughout the app.
         */
        inline fun <reified T : BaseActivity> setupMainActivity() {
            clazz = T::class.java
        }

    }

    lateinit var application: Application

    lateinit var dagger: Application

    val context: Context
        get() = application
}