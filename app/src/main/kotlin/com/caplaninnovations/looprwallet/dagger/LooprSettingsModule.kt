package com.caplaninnovations.looprwallet.dagger

import android.content.Context
import com.caplaninnovations.looprwallet.models.android.settings.CurrencySettings
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettings
import com.caplaninnovations.looprwallet.models.android.settings.ThemeSettings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Corey Caplan on 2/3/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@Module
class LooprSettingsModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideLooprThemeSettings(): ThemeSettings {
        return ThemeSettings(LooprSettings.getInstance(context))
    }

    @Singleton
    @Provides
    fun provideCurrencySettings(): CurrencySettings {
        return CurrencySettings(LooprSettings.getInstance(context))
    }

}