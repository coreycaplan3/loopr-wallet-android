package com.caplaninnovations.looprwallet.dagger

import android.support.test.InstrumentationRegistry
import com.caplaninnovations.looprwallet.application.TestLooprWalletApp
import com.caplaninnovations.looprwallet.models.android.settings.WalletSettings
import junit.framework.Assert.assertTrue
import org.junit.Before
import java.util.concurrent.FutureTask
import javax.inject.Inject

/**
 *  Created by Corey on 2/3/2018
 *
 *  Project: loopr-wallet-android
 *
 *  Purpose of Class:
 *
 *
 */
open class BaseDaggerTest {

    @Inject
    lateinit var walletSettings: WalletSettings

    lateinit var component: LooprTestComponent

    @Before
    fun baseDaggerSetup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        val app = (context as TestLooprWalletApp)
        component = app.provideDaggerComponent() as LooprTestComponent
        component.inject(this)

        putDefaultWallet()
    }

    open fun putDefaultWallet() {
        val didCreateWallet = walletSettings.createWallet("corey-test")
        assertTrue(didCreateWallet)
    }

    fun noOp() {
        // This function is a no-op, used to prevent an exception being thrown for there being no
        // test methods in this class (but it uses the AndroidJUnit4 Test Runner)
    }

    fun waitForAnimationsAndTask(task: FutureTask<*>) {
        task.get()
        Thread.sleep(300)
    }

}