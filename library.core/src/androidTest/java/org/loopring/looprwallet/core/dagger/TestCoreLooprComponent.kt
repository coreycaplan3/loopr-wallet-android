package org.loopring.looprwallet.core.dagger

import dagger.Component
import org.loopring.looprwallet.core.fragments.security.ConfirmOldSecurityFragmentTest
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
@LooprComponentScope
@Component(dependencies = [CoreLooprComponent::class])
interface TestCoreLooprComponent {

    fun inject(baseDaggerTest: BaseDaggerTest)
    fun inject(confirmOldSecurityFragmentTest: ConfirmOldSecurityFragmentTest)

    fun inject(themeSettingsTest: ThemeSettingsTest)

    fun inject(walletSettingsTest: UserWalletSettingsTest)
}