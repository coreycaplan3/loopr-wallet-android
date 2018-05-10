package org.loopring.looprwallet.core.dagger

import dagger.Module
import dagger.Provides
import org.loopring.looprwallet.core.models.settings.LooprSettings
import org.loopring.looprwallet.core.models.settings.LoopringNetworkSettings
import org.web3j.protocol.Web3j
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.http.HttpService
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by corey on 5/9/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
@Module(includes = [LooprSettingsModule::class])
class LooprNetworkModule {

    @Provides
    @Singleton
    fun provideWeb3j(settings: LoopringNetworkSettings): Web3j {
        return Web3jFactory.build(HttpService(settings.currentRelay))
    }

}