package com.caplaninnovations.looprwallet.dagger

import android.content.Context
import com.caplaninnovations.looprwallet.models.android.settings.LooprSecureSettings
import com.caplaninnovations.looprwallet.models.android.settings.WalletSettings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Corey on 3/24/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
@Module
class LooprSecureSettingsModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideLooprWalletSettings(): WalletSettings {
        return WalletSettings(LooprSecureSettings.getInstance(context))
    }

}