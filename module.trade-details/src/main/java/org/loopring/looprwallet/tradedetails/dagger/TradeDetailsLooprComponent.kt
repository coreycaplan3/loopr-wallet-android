package org.loopring.looprwallet.tradedetails.dagger

import dagger.Component
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.CoreLooprComponent
import org.loopring.looprwallet.core.dagger.LooprComponentScope
import org.loopring.looprwallet.tradedetails.fragments.TradingPairDetailsFragment

/**
 * Created by Corey on 4/23/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
@LooprComponentScope
@Component(dependencies = [CoreLooprComponent::class])
interface TradeDetailsLooprComponent {

    fun inject(tradingPairDetailsFragment: TradingPairDetailsFragment)

}

interface TradeDetailsLooprComponentProvider {

    fun provideTradeDetailsLooprComponent(): TradeDetailsLooprComponent
}

val tradeDetailsLooprComponent: TradeDetailsLooprComponent
    get() = (CoreLooprWalletApp.application as TradeDetailsLooprComponentProvider).provideTradeDetailsLooprComponent()