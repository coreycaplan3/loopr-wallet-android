package com.caplaninnovations.looprwallet.dagger

import android.content.Context
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettings
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettingsImplTest
import com.caplaninnovations.looprwallet.models.security.SecurityClient
import com.caplaninnovations.looprwallet.models.security.SecurityClientImplTest
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 *  Created by Corey on 2/5/2018
 *
 *  Project: loopr-wallet-android
 *
 *  Purpose of Class:
 *
 *
 */
@Module
class LooprSecurityTestModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideSecurityModule(): SecurityClient {
        return SecurityClientImplTest(LooprSettingsImplTest())
    }
}