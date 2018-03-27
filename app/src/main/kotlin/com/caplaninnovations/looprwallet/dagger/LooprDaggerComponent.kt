package com.caplaninnovations.looprwallet.dagger

import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.activities.SettingsActivity
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.fragments.security.EnterNewSecurityFragment
import com.caplaninnovations.looprwallet.fragments.settings.HomeSettingsFragment
import com.caplaninnovations.looprwallet.fragments.settings.SecuritySettingsFragment
import com.caplaninnovations.looprwallet.fragments.transfers.CreateTransferAmountFragment
import com.caplaninnovations.looprwallet.models.android.settings.*
import com.caplaninnovations.looprwallet.models.security.WalletClient
import com.caplaninnovations.looprwallet.realm.RealmClient
import com.caplaninnovations.looprwallet.repositories.BaseRealmRepository
import com.caplaninnovations.looprwallet.viewmodels.price.CurrencyExchangeRateViewModel
import dagger.Component
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

    fun inject(settingsActivity: SettingsActivity)

    // Applications
    fun inject(looprWalletApp: LooprWalletApp)

    // Fragments
    fun inject(homeSettingsFragment: HomeSettingsFragment)

    fun inject(securitySettingsFragment: SecuritySettingsFragment)
    fun inject(createTransferAmountFragment: CreateTransferAmountFragment)
    fun inject(enterNewSecurityFragment: EnterNewSecurityFragment)

    // Repositories
    fun inject(baseRealmRepository: BaseRealmRepository)

    // View Models
    fun inject(currencyExchangeRateViewModel: CurrencyExchangeRateViewModel)

    val userWalletSettings: UserWalletSettings
    val themeSettings: ThemeSettings
    val realmClient: RealmClient
    val currencySettings: CurrencySettings
    val securitySettings: SecuritySettings
    val ethereumFeeSettings: EthereumFeeSettings
    val loopringFeeSettings: LoopringFeeSettings
    val ethereumNetworkSettings: EthereumNetworkSettings
    val loopringNetworkSettings: LoopringNetworkSettings
    val generalWalletSettings: GeneralWalletSettings
    val userPinSettings: UserPinSettings
    val walletClient: WalletClient
    val web3j: Web3j
}