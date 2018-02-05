package com.caplaninnovations.looprwallet.dagger

import android.content.Context
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettings
import com.caplaninnovations.looprwallet.models.android.settings.ThemeSettings
import com.caplaninnovations.looprwallet.models.android.settings.WalletSettings
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
class LooprSettingsModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideLooprWalletSettings(): WalletSettings {
        return WalletSettings(LooprSettings.getInstance(context))
    }

    @Singleton
    @Provides
    fun provideLooprThemeSettings(): ThemeSettings {
        return ThemeSettings(LooprSettings.getInstance(context))
    }

}