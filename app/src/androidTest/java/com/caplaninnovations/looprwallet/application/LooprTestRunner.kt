package com.caplaninnovations.looprwallet.application

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner
import com.caplaninnovations.looprwallet.utilities.logv

/**
 * Created by Corey Caplan on 2/3/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class: To override the standard JUnit runner and provide an instance of our test
 * application class instead of the default class.
 *
 * **NOTE** this class's package and path correspond to the path in the app .gradle file.
 */
class LooprTestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        // Return our test application
        logv("Overriding ${AndroidJUnitRunner::class.java.simpleName} with ${LooprTestRunner::class.java.simpleName}")
        return super.newApplication(cl, TestLooprWalletApp::class.java.name, context)
    }

}