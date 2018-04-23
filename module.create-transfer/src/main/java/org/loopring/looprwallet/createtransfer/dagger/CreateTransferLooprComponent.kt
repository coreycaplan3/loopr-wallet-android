package org.loopring.looprwallet.createtransfer.dagger

import dagger.Component
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.CoreLooprComponent
import org.loopring.looprwallet.core.dagger.LooprComponentScope
import org.loopring.looprwallet.createtransfer.fragments.CreateTransferAmountFragment

/**
 * Created by Corey on 4/3/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@LooprComponentScope
@Component(dependencies = [CoreLooprComponent::class])
interface CreateTransferLooprComponent {

    fun inject(createTransferAmountFragment: CreateTransferAmountFragment)
}

interface CreateTransferLooprComponentProvider {

    fun provideCreateTransferLooprComponent(): CreateTransferLooprComponent
}

val createTransferLooprComponent: CreateTransferLooprComponent
    get() = (CoreLooprWalletApp.application as CreateTransferLooprComponentProvider).provideCreateTransferLooprComponent()