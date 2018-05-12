package org.loopring.looprwallet.homeorders.dagger

import dagger.Component
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.CoreLooprComponent
import org.loopring.looprwallet.core.dagger.LooprComponentScope
import org.loopring.looprwallet.homeorders.adapters.HomeOrderViewHolder

/**
 * Created by Corey on 4/24/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
@LooprComponentScope
@Component(dependencies = [CoreLooprComponent::class])
interface HomeOrdersLooprComponent {

    fun inject(homeOrderViewHolder: HomeOrderViewHolder)
}

interface HomeOrdersLooprComponentProvider {

    fun provideHomeOrdersLooprComponent(): HomeOrdersLooprComponent
}

val homeOrdersLooprComponent: HomeOrdersLooprComponent
    get() = (CoreLooprWalletApp.application as HomeOrdersLooprComponentProvider).provideHomeOrdersLooprComponent()