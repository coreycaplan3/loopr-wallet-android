package com.caplaninnovations.looprwallet.dagger

import com.caplaninnovations.looprwallet.models.security.SecurityClientTestImpl
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettingsTestImpl
import com.caplaninnovations.looprwallet.models.security.SecurityClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Corey on 2/5/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@Module
class LooprSecurityTestModule {

    @Singleton
    @Provides
    fun provideSecurityModule(): SecurityClient {
        return SecurityClientTestImpl(LooprSettingsTestImpl())
    }
}