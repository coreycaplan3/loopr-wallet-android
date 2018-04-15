package org.loopring.looprwallet.dagger

import dagger.Component
import org.loopring.looprwallet.application.LooprWalletApp
import org.loopring.looprwallet.core.dagger.CoreLooprComponent
import org.loopring.looprwallet.core.dagger.LooprComponentScope

/**
 * Created by Corey on 4/3/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@LooprComponentScope
@Component(dependencies = [CoreLooprComponent::class])
interface AppLooprComponent {

    fun inject(looprWalletApp: LooprWalletApp)
}