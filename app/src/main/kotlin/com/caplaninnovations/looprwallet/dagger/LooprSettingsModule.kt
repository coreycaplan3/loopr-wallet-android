package com.caplaninnovations.looprwallet.dagger

import android.content.Context
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettings
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettingsImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Corey Caplan on 2/3/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */

@Module
open class LooprSettingsModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideLooprSettings(): LooprSettings = LooprSettingsImpl(context)

}