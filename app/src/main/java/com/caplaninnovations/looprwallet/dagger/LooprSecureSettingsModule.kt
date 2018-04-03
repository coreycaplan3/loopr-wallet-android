package com.caplaninnovations.looprwallet.dagger

import android.content.Context
import org.loopring.looprwallet.core.models.settings.LooprSecureSettings
import org.loopring.looprwallet.core.models.settings.UserPinSettings
import org.loopring.looprwallet.core.models.settings.UserWalletSettings
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