package org.loopring.looprwallet.homemywallet.dagger

import dagger.Component
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.CoreLooprComponent
import org.loopring.looprwallet.core.dagger.LooprComponentScope
import org.loopring.looprwallet.homemywallet.fragments.HomeMyWalletFragment

/**
 * Created by Corey on 4/17/2018
 *
 * Project: loopr-android-official
 *
 * Purpose of Class:
 *
 *
 */
@LooprComponentScope
@Component(dependencies = [CoreLooprComponent::class])
interface HomeMyWalletLooprComponent {

    fun inject(homeMyWalletFragment: HomeMyWalletFragment)
}

interface HomeMyWalletLooprComponentProvider {

    fun provideHomeMyWalletLooprComponent(): HomeMyWalletLooprComponent
}

val homeMyWalletLooprComponent: HomeMyWalletLooprComponent
    get() = (CoreLooprWalletApp.application as HomeMyWalletLooprComponentProvider).provideHomeMyWalletLooprComponent()