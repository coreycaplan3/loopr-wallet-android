package com.caplaninnovations.looprwallet.dagger

import com.caplaninnovations.looprwallet.realm.RealmClient
import com.caplaninnovations.looprwallet.realm.RealmClientImplTest
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 *  Created by Corey on 2/5/2018
 *
 *  Project: loopr-wallet-android
 *
 *  Purpose of Class:
 *
 *
 */
@Module
class LooprRealmTestModule {

    @Singleton
    @Provides
    fun provideLooprRealmClient(): RealmClient = RealmClientImplTest()

}