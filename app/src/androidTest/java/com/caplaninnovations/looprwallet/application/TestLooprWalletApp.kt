package com.caplaninnovations.looprwallet.application

import com.caplaninnovations.looprwallet.dagger.*

/**
 * Created by Corey on 2/3/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class TestLooprWalletApp : LooprWalletApp() {

    override fun provideDaggerComponent(): LooprDaggerComponent {
        return DaggerLooprTestComponent.builder()
                .looprRealmModule(LooprRealmModule())
                .looprSecurityModule(LooprWalletModule(this.applicationContext))
                .looprSettingsModule(LooprSettingsModule(this.applicationContext))
                .looprSecureSettingsModule(LooprSecureSettingsModule(this.applicationContext))
                .looprEthModule(LooprEthModule())
                .build()
    }
}