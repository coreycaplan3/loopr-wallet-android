package com.caplaninnovations.looprwallet.dagger

import com.caplaninnovations.looprwallet.realm.RealmClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Corey on 2/4/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@Module
class LooprRealmModule {

    @Singleton
    @Provides
    fun provideLooprRealmClient(): RealmClient = RealmClient.getInstance()

}