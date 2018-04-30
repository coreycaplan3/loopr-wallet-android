package org.loopring.looprwallet.orderdetails.dagger

import dagger.Component
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.CoreLooprComponent
import org.loopring.looprwallet.core.dagger.LooprComponentScope
import org.loopring.looprwallet.orderdetails.adapters.OrderFillViewHolder
import org.loopring.looprwallet.orderdetails.adapters.OrderSummaryViewHolder
import org.loopring.looprwallet.orderdetails.fragments.OrderDetailsFragment

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
interface OrderDetailsLooprComponent {

    fun inject(orderDetailsFragment: OrderDetailsFragment)
    fun inject(viewHolder: OrderSummaryViewHolder)
    fun inject(viewHolder: OrderFillViewHolder)
}

interface OrderDetailsLooprComponentProvider {

    fun provideOrderDetailsLooprComponent(): OrderDetailsLooprComponent
}

val orderDetailsLooprComponent: OrderDetailsLooprComponent
    get() = (CoreLooprWalletApp.application as OrderDetailsLooprComponentProvider).provideOrderDetailsLooprComponent()