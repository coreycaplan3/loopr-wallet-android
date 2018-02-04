package com.caplaninnovations.looprwallet.application

import com.caplaninnovations.looprwallet.dagger.DaggerLooprTestComponent
import com.caplaninnovations.looprwallet.dagger.LooprProductionComponent
import com.caplaninnovations.looprwallet.dagger.LooprSettingsTestModule

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
                .build()
    }
}