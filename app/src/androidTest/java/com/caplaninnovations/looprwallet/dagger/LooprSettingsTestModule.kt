package com.caplaninnovations.looprwallet.dagger

import com.caplaninnovations.looprwallet.models.android.settings.LooprSettings
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettingsImplTest
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 *  Created by Corey on 2/3/2018
 *
 *  Project: loopr-wallet-android
 *
 *  Purpose of Class:
 *
 *
 */
@Module
class LooprSettingsTestModule {

    @Singleton
    @Provides
    fun provideLooprSettings(): LooprSettings = LooprSettingsImplTest()

}