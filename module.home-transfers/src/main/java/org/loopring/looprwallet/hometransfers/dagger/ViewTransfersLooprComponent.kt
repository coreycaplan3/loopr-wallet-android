package org.loopring.looprwallet.hometransfers.dagger

import dagger.Component
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.CoreLooprComponent
import org.loopring.looprwallet.core.dagger.LooprComponentScope
import org.loopring.looprwallet.hometransfers.adapters.ViewTransfersViewHolder

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
interface HomeTransfersLooprComponent {

    fun inject(viewTransfersViewHolder: ViewTransfersViewHolder)
}

interface HomeTransfersLooprComponentProvider {

    fun provideHomeTransfersLooprComponent(): HomeTransfersLooprComponent
}

val homeTransfersLooprComponent: HomeTransfersLooprComponent
    get() = (CoreLooprWalletApp.application as HomeTransfersLooprComponentProvider).provideHomeTransfersLooprComponent()