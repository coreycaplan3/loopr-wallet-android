package com.caplaninnovations.looprwallet.dagger

import com.caplaninnovations.looprwallet.models.android.settings.LooprSettings
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettingsImplTest
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Corey Caplan on 2/3/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
@Module
class LooprTestingModule() {

    @Singleton
    @Provides
    fun provideLooprSettings(): LooprSettings {
        return LooprSettingsImplTest()
    }

}