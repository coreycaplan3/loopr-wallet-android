package com.caplaninnovations.looprwallet.application

import android.support.multidex.MultiDexApplication
import com.caplaninnovations.looprwallet.BuildConfig
import com.caplaninnovations.looprwallet.utilities.logi
import com.google.firebase.crash.FirebaseCrash

/**
 * Created by Corey on 1/18/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
class LooprWalletApp : MultiDexApplication() {

    companion object {
        lateinit var application: LooprWalletApp
    }

    override fun onCreate() {
        super.onCreate()

        logi("Creating Application...")

        application = this

        FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG)
    }

}