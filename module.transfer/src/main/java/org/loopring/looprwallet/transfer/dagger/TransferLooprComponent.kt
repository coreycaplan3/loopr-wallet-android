package org.loopring.looprwallet.transfer.dagger

import dagger.Component
import org.loopring.looprwallet.core.application.LooprWalletCoreApp
import org.loopring.looprwallet.core.dagger.CoreLooprComponent
import org.loopring.looprwallet.core.dagger.LooprComponentScope
import org.loopring.looprwallet.transfer.fragments.CreateTransferAmountFragment

/**
 * Created by Corey on 4/3/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@LooprComponentScope
@Component(dependencies = [CoreLooprComponent::class])
interface TransferLooprComponent {

    fun inject(createTransferAmountFragment: CreateTransferAmountFragment)
}

interface TransferLooprComponentProvider {

    fun provideTransferLooprComponent(): TransferLooprComponent
}

val transferLooprComponent: TransferLooprComponent
    get() = (LooprWalletCoreApp.application as TransferLooprComponentProvider).provideTransferLooprComponent()