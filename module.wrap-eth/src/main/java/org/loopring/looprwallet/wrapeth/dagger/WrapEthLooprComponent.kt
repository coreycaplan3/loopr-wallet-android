package org.loopring.looprwallet.wrapeth.dagger

import dagger.Component
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.CoreLooprComponent
import org.loopring.looprwallet.core.dagger.LooprComponentScope
import org.loopring.looprwallet.wrapeth.fragments.WrapEthFragment
import org.loopring.looprwallet.wrapeth.viewmodels.WrapEthViewModel

/**
 * Created by corey on 5/10/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
@LooprComponentScope
@Component(dependencies = [CoreLooprComponent::class])
interface WrapEthLooprComponent {

    fun inject(wrapEthFragment: WrapEthFragment)

    fun inject(wrapEthViewModel: WrapEthViewModel)
}

interface WrapEthLooprComponentProvider {

    fun provideWrapEthLooprComponent(): WrapEthLooprComponent
}

val wrapEthLooprComponent: WrapEthLooprComponent
    get() = (CoreLooprWalletApp.application as WrapEthLooprComponentProvider).provideWrapEthLooprComponent()