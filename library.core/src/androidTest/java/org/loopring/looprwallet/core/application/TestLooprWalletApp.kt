package org.loopring.looprwallet.core.application

import android.support.multidex.MultiDexApplication
import org.loopring.looprwallet.core.extensions.logi

/**
 * Created by Corey on 4/6/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
class TestLooprWalletApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        CoreLooprWalletApp.application = this

        logi("Creating ${TestLooprWalletApp::class.simpleName}...")
    }

}