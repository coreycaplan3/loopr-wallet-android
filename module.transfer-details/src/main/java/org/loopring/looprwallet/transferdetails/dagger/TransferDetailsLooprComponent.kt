package org.loopring.looprwallet.transferdetails.dagger

import dagger.Component
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.CoreLooprComponent
import org.loopring.looprwallet.core.dagger.LooprComponentScope
import org.loopring.looprwallet.transferdetails.dialogs.TransferDetailsDialog

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
interface TransferDetailsLooprComponent {
    fun inject(transferDetailsDialog: TransferDetailsDialog)

}

interface TransferDetailsLooprComponentProvider {

    fun provideTransferDetailsLooprComponent(): TransferDetailsLooprComponent
}

val transferDetailsLooprComponent: TransferDetailsLooprComponent
    get() = (CoreLooprWalletApp.application as TransferDetailsLooprComponentProvider).provideTransferDetailsLooprComponent()