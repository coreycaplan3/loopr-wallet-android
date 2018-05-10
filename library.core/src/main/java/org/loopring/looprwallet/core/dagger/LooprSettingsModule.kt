package org.loopring.looprwallet.core.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import org.loopring.looprwallet.core.models.settings.*
import javax.inject.Named
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
    fun provideSecuritySettings(): SecuritySettings {
        return SecuritySettings(LooprSettings.getInstance(context))
    }

    @Singleton
    @Provides
    fun provideEthereumFeeSettings(): EthereumFeeSettings {
        return EthereumFeeSettings(LooprSettings.getInstance(context))
    }

    @Singleton
    @Provides
    fun provideLoopringFeeSettings(): LoopringFeeSettings {
        return LoopringFeeSettings(LooprSettings.getInstance(context))
    }

    @Singleton
    @Provides
    fun provideGeneralWalletSettings(): GeneralWalletSettings {
        return GeneralWalletSettings(LooprSettings.getInstance(context))
    }

    @Singleton
    @Provides
    fun provideCurrencySettings(): CurrencySettings {
        return CurrencySettings(LooprSettings.getInstance(context))
    }

    @Singleton
    @Provides
    fun provideLoopringNetworkSettings(): LoopringNetworkSettings {
        return LoopringNetworkSettings(LooprSettings.getInstance(context))
    }

}