package com.caplaninnovations.looprwallet.dagger

import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettings
import dagger.Component
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Corey Caplan on 2/3/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
@Singleton
@Component(modules = [LooprSettingsModule::class])
interface LooprProductionComponent {

    fun inject(baseActivity: BaseActivity)
    fun inject(app: LooprWalletApp)

    fun getLooprSettings(): LooprSettings
}