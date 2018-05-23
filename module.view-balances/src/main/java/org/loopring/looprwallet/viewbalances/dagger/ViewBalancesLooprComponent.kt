package org.loopring.looprwallet.viewbalances.dagger

import dagger.Component
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.CoreLooprComponent
import org.loopring.looprwallet.core.dagger.LooprComponentScope
import org.loopring.looprwallet.viewbalances.adapters.ViewBalancesAdapter
import org.loopring.looprwallet.viewbalances.adapters.ViewBalancesViewHolder
import org.loopring.looprwallet.viewbalances.fragments.ViewBalancesFragment

/**
 * Created by Corey on 4/18/2018
 *
 * Project: loopr-android-official
 *
 * Purpose of Class:
 *
 *
 */
@LooprComponentScope
@Component(dependencies = [CoreLooprComponent::class])
interface ViewBalancesLooprComponent {

    fun inject(viewBalancesFragment: ViewBalancesFragment)

    fun inject(viewBalancesAdapter: ViewBalancesAdapter)

    fun inject(viewBalancesViewHolder: ViewBalancesViewHolder)
}

interface ViewBalancesLooprComponentProvider {

    fun provideViewBalancesLooprComponent(): ViewBalancesLooprComponent
}

val viewBalancesLooprComponent: ViewBalancesLooprComponent
    get() = (CoreLooprWalletApp.application as ViewBalancesLooprComponentProvider).provideViewBalancesLooprComponent()