package com.caplaninnovations.looprwallet.dagger

import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettingsImplTest
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Corey Caplan on 2/3/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
@Singleton
@Component(modules = [LooprSettingsImplTest::class])
interface LooprTestingComponent {

    fun inject(activity: BaseActivity)
}