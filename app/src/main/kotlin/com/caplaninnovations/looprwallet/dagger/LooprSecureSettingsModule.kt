package com.caplaninnovations.looprwallet.dagger

import android.content.Context
import com.caplaninnovations.looprwallet.models.android.settings.LooprSecureSettings
import com.caplaninnovations.looprwallet.models.android.settings.UserPinSettings
import com.caplaninnovations.looprwallet.models.android.settings.UserWalletSettings
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
    fun provideUserWalletSettings(): UserWalletSettings {
        return UserWalletSettings(LooprSecureSettings.getInstance(context))
    }

    @Singleton
    @Provides
    fun provideUserPinSettings(): UserPinSettings {
        return UserPinSettings(LooprSecureSettings.getInstance(context))
    }

}