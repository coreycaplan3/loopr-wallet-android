package com.caplaninnovations.looprwallet.dagger

import com.caplaninnovations.looprwallet.models.android.settings.LooprSettingsTestImpl
import com.caplaninnovations.looprwallet.models.android.settings.ThemeSettings
import com.caplaninnovations.looprwallet.models.android.settings.WalletSettings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Corey on 2/3/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@Module
class LooprSettingsTestModule {

    @Singleton
    @Provides
    fun provideThemeSettings(): ThemeSettings = ThemeSettings(LooprSettingsTestImpl())

    @Singleton
    @Provides
    fun provideWalletSettings(): WalletSettings = WalletSettings(LooprSettingsTestImpl())

}