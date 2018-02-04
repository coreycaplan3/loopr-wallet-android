package com.caplaninnovations.looprwallet.dagger

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.application.TestLooprWalletApp
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettings
import com.caplaninnovations.looprwallet.models.android.settings.LooprWalletSettings
import com.caplaninnovations.looprwallet.utilities.loge
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.runner.RunWith
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
@RunWith(AndroidJUnit4::class)
abstract class BaseDaggerTest {

    @Inject
    lateinit var looprSettings: LooprSettings

    val defaultWalletName = "corey-test"

    @Before
    fun baseDaggerSetup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        val app = (context as TestLooprWalletApp)
        val component = app.provideDaggerComponent() as LooprTestComponent
        component.inject(this)

        putDefaultWallet()
    }

    open fun putDefaultWallet() {
        loge("PUT DEFAULT WALLET")
        val didCreateWallet = LooprWalletSettings(looprSettings).createWallet(defaultWalletName)
        assertTrue(didCreateWallet)
    }

}