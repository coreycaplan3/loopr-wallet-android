package org.loopring.looprwallet.core.application

import org.loopring.looprwallet.core.extensions.logi

/**
 * Created by Corey on 4/6/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class TestCoreLooprWalletApp : CoreLooprWalletApp() {

    override fun onCreate() {
        super.onCreate()

        logi("Creating ${TestCoreLooprWalletApp::class.simpleName}...")
    }

}