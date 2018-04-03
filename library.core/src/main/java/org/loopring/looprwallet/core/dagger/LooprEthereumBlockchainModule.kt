package org.loopring.looprwallet.core.dagger

import com.caplaninnovations.looprwallet.BuildConfig
import dagger.Module
import dagger.Provides
import org.web3j.protocol.Web3j
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.http.HttpService
import javax.inject.Singleton


/**
 * Created by Corey on 2/27/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@Module
class LooprEthereumBlockchainModule {

    @Provides
    @Singleton
    fun provideWeb3j(): Web3j = Web3jFactory.build(HttpService(BuildConfig.ETHEREUM_URL))

}