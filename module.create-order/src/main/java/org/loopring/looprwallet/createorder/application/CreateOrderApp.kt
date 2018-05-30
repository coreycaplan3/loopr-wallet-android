package org.loopring.looprwallet.createorder.application

import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.createorder.dagger.CreateOrderLooprComponent
import org.loopring.looprwallet.createorder.dagger.CreateOrderLooprComponentProvider
import org.loopring.looprwallet.createorder.dagger.DaggerCreateOrderLooprComponent

/**
 * Created by corey on 5/30/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class CreateOrderApp: CoreLooprWalletApp(), CreateOrderLooprComponentProvider {

    companion object {

        val createOrderLooprComponent: CreateOrderLooprComponent by lazy {
            DaggerCreateOrderLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
        }

    }

    override fun provideCreateOrderLooprComponent() = createOrderLooprComponent

}