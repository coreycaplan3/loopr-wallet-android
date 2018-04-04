package org.loopring.looprwallet.core.dagger

import dagger.Subcomponent
import org.loopring.looprwallet.core.models.settings.ThemeSettingsTest
import org.loopring.looprwallet.core.models.settings.UserWalletSettingsTest

/**
 * Created by Corey on 2/3/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@Subcomponent
interface CoreLooprTestComponent : CoreLooprComponent {

    fun inject(baseDaggerTest: BaseDaggerTest)
    fun inject(themeSettingsTest: ThemeSettingsTest)
    fun inject(walletSettingsTest: UserWalletSettingsTest)
}