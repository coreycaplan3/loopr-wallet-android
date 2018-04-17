package org.loopring.looprwallet.homeorders.dagger

import dagger.Component
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.CoreLooprComponent
import org.loopring.looprwallet.core.dagger.LooprComponentScope

/**
 * Created by Corey on 4/9/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@LooprComponentScope
@Component(dependencies = [CoreLooprComponent::class])
interface HomeOrdersLooprComponent {

}

interface HomeOrdersLooprComponentProvider {

    fun provideHomeOrdersLooprComponent(): HomeOrdersLooprComponent
}

val homeOrdersLooprComponent: HomeOrdersLooprComponent
    get() = (CoreLooprWalletApp.application as HomeOrdersLooprComponentProvider).provideHomeOrdersLooprComponent()