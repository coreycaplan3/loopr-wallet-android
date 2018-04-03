package org.loopring.looprwallet.core.dagger

import dagger.Component
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.models.settings.*
import org.loopring.looprwallet.core.realm.RealmClient
import org.loopring.looprwallet.core.repositories.BaseRealmRepository
import org.loopring.looprwallet.core.viewmodels.price.CurrencyExchangeRateViewModel
import org.loopring.looprwallet.core.wallet.WalletClient
import org.web3j.protocol.Web3j
import javax.inject.Singleton

/**
 * Created by Corey Caplan on 2/3/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To provide classes related to settings and the database (pretty much, data).
 *
 */
@Singleton
@Component(modules = [LooprSettingsModule::class, LooprSecureSettingsModule::class,
    LooprRealmModule::class, LooprWalletModule::class, LooprEthereumBlockchainModule::class])
interface LooprDaggerComponent {

    // Activities
    fun inject(baseActivity: BaseActivity)

    // Repositories
    fun inject(baseRealmRepository: BaseRealmRepository)

    // View Models
    fun inject(currencyExchangeRateViewModel: CurrencyExchangeRateViewModel)

    // Values - Settings
    val userWalletSettings: UserWalletSettings
    val themeSettings: ThemeSettings
    val currencySettings: CurrencySettings
    val securitySettings: SecuritySettings
    val ethereumFeeSettings: EthereumFeeSettings
    val loopringFeeSettings: LoopringFeeSettings
    val ethereumNetworkSettings: EthereumNetworkSettings
    val loopringNetworkSettings: LoopringNetworkSettings
    val generalWalletSettings: GeneralWalletSettings
    val userPinSettings: UserPinSettings

    // Values - Clients
    val realmClient: RealmClient
    val walletClient: WalletClient

    // Values - Networking
    val web3j: Web3j
}