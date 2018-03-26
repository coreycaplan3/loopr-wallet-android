package com.caplaninnovations.looprwallet.dagger

import android.content.Context
import com.caplaninnovations.looprwallet.models.android.settings.LooprSecureSettings
import com.caplaninnovations.looprwallet.models.security.WalletClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Corey on 2/4/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@Module
class LooprWalletModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideSecurityModule(): WalletClient {
        return WalletClient.getInstance(context, LooprSecureSettings.getInstance(context))
    }
}