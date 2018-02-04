package com.caplaninnovations.looprwallet.dagger

import dagger.Component
import javax.inject.Singleton

/**
 *  Created by Corey on 2/3/2018
 *
 *  Project: loopr-wallet-android
 *
 *  Purpose of Class:
 *
 *
 */
@Singleton
@Component(modules = [(LooprSettingsTestModule::class)])
interface LooprTestComponent : LooprProductionComponent {

    fun inject(baseDaggerTest: BaseDaggerTest)
}