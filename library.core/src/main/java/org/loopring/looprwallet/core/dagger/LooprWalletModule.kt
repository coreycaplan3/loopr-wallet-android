package org.loopring.looprwallet.core.dagger

import android.content.Context
import org.loopring.looprwallet.core.models.settings.LooprSecureSettings
import org.loopring.looprwallet.core.wallet.WalletClient
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