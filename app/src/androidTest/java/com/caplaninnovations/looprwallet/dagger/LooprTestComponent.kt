package com.caplaninnovations.looprwallet.dagger

import com.caplaninnovations.looprwallet.models.android.settings.ThemeSettingsTest
import com.caplaninnovations.looprwallet.models.android.settings.UserWalletSettingsTest
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Corey on 2/3/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@Singleton
@Component(modules = [LooprSettingsModule::class, LooprSecureSettingsModule::class,
    LooprRealmModule::class, LooprWalletModule::class, LooprEthereumBlockchainModule::class])
interface LooprTestComponent : LooprDaggerComponent {

    fun inject(baseDaggerTest: BaseDaggerTest)
    fun inject(themeSettingsTest: ThemeSettingsTest)
    fun inject(walletSettingsTest: UserWalletSettingsTest)
}