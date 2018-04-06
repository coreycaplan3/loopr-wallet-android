package org.loopring.looprwallet.core.application

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner

/**
 * Created by Corey on 4/6/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@Suppress("unused")
class LooprJUnitRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, TestCoreLooprWalletApp::class.java.name, context)
    }
}