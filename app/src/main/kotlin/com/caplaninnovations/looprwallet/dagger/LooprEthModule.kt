package com.caplaninnovations.looprwallet.dagger

import dagger.Module
import dagger.Provides
import org.web3j.protocol.Web3j
import javax.inject.Singleton
import org.web3j.protocol.http.HttpService
import org.web3j.protocol.Web3jFactory


/**
 * Created by Corey on 2/27/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@Module
class LooprEthModule {

    @Provides
    @Singleton
    fun provideWeb3j(): Web3j = Web3jFactory.build(HttpService())

}