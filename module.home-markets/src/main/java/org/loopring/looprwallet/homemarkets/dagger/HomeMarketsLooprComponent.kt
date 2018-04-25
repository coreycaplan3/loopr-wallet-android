package org.loopring.looprwallet.homemarkets.dagger

import dagger.Component
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.CoreLooprComponent
import org.loopring.looprwallet.core.dagger.LooprComponentScope
import org.loopring.looprwallet.homemarkets.adapters.MarketsViewHolder

/**
 * Created by Corey on 4/25/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
@LooprComponentScope
@Component(dependencies = [CoreLooprComponent::class])
interface HomeMarketsLooprComponent {

    fun inject(marketsViewHolder: MarketsViewHolder)
}

interface HomeMarketsLooprComponentProvider {

    fun provideHomeMarketsLooprComponent(): HomeMarketsLooprComponent
}

val homeMarketsLooprComponent: HomeMarketsLooprComponent
    get() = (CoreLooprWalletApp.application as HomeMarketsLooprComponentProvider).provideHomeMarketsLooprComponent()