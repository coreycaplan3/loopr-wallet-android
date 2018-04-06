package org.loopring.looprwallet.walletsignin.dagger

import dagger.Component
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.CoreLooprComponent
import org.loopring.looprwallet.core.dagger.LooprComponentScope
import org.loopring.looprwallet.walletsignin.viewmodels.WalletGeneratorViewModel

/**
 * Created by Corey on 4/4/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@LooprComponentScope
@Component(dependencies = [CoreLooprComponent::class])
interface WalletLooprComponent {

    fun inject(walletGeneratorViewModel: WalletGeneratorViewModel)
}

interface WalletLooprComponentProvider {

    fun provideWalletLooprComponent(): WalletLooprComponent
}

val walletLooprComponent: WalletLooprComponent
    get() = (CoreLooprWalletApp.application as WalletLooprComponentProvider).provideWalletLooprComponent()