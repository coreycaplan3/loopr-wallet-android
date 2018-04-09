package org.loopring.looprwallet.homeorders.dagger

import dagger.Component
import io.realm.RealmModel
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.CoreLooprComponent
import org.loopring.looprwallet.core.dagger.LooprComponentScope
import org.loopring.looprwallet.homeorders.adapters.GeneralOrderAdapter

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
interface HomeOrdersLooprComponent {

    fun inject(generalOrderAdapter: GeneralOrderAdapter)
}

interface HomeOrdersLooprComponentProvider {

    fun provideHomeOrdersLooprComponent(): HomeOrdersLooprComponent
}

val homeOrdersLooprComponent: HomeOrdersLooprComponent
    get() = (CoreLooprWalletApp.application as HomeOrdersLooprComponentProvider).provideHomeOrdersLooprComponent()