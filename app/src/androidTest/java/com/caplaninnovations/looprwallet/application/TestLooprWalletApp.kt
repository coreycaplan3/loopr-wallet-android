package com.caplaninnovations.looprwallet.application

import com.caplaninnovations.looprwallet.dagger.*

/**
 *  Created by Corey on 2/3/2018
 *
 *  Project: loopr-wallet-android
 *
 *  Purpose of Class:
 *
 *
 */
class TestLooprWalletApp : LooprWalletApp() {

    override fun provideDaggerComponent(): LooprProductionComponent {
        return DaggerLooprTestComponent.builder()
                .looprSettingsTestModule(LooprSettingsTestModule())
                .looprRealmTestModule(LooprRealmTestModule())
                .looprSecurityTestModule(LooprSecurityTestModule(applicationContext))
                .build()
    }
}