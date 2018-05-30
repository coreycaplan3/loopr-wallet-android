package org.loopring.looprwallet.createorder.dagger

import dagger.Component
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.CoreLooprComponent
import org.loopring.looprwallet.core.dagger.LooprComponentScope
import org.loopring.looprwallet.createorder.fragments.ConfirmOrderFragment
import org.loopring.looprwallet.createorder.fragments.CreateOrderFragment

/**
 * Created by corey on 5/29/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
@LooprComponentScope
@Component(dependencies = [CoreLooprComponent::class])
interface CreateOrderLooprComponent {

    fun inject(createOrderFragment: CreateOrderFragment)
    fun inject(confirmOrderFragment: ConfirmOrderFragment)

}

interface CreateOrderLooprComponentProvider {

    fun provideCreateOrderLooprComponent(): CreateOrderLooprComponent
}

val createOrderLooprComponent: CreateOrderLooprComponent
    get() = (CoreLooprWalletApp.application as CreateOrderLooprComponentProvider).provideCreateOrderLooprComponent()