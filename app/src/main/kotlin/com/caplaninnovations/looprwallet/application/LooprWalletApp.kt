package com.caplaninnovations.looprwallet.application

import android.app.Application
import com.caplaninnovations.looprwallet.BuildConfig
import com.caplaninnovations.looprwallet.utilities.logi
import com.google.firebase.crash.FirebaseCrash

/**
 * Created by Corey on 1/18/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
class LooprWalletApp: Application() {

    override fun onCreate() {
        super.onCreate()

        logi("Creating Application...")
        FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG)
    }

}