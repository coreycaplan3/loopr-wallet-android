package org.loopring.looprwallet.core.dagger

import dagger.Component
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.activities.SettingsActivity
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dialogs.BaseBottomSheetDialog
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.fragments.security.BaseSecurityFragment
import org.loopring.looprwallet.core.fragments.settings.EthereumFeeSettingsFragment
import org.loopring.looprwallet.core.fragments.settings.HomeSettingsFragment
import org.loopring.looprwallet.core.fragments.settings.LoopringFeeSettingsFragment
import org.loopring.looprwallet.core.fragments.settings.SecuritySettingsFragment
import org.loopring.looprwallet.core.models.settings.*
import org.loopring.looprwallet.core.networking.eth.EthereumService
import org.loopring.looprwallet.core.presenters.TokenNumberPadPresenter
import org.loopring.looprwallet.core.realm.RealmClient
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
@Component(modules = [LooprSettingsModule::class, LooprNetworkModule::class,
    LooprSecureSettingsModule::class, LooprRealmModule::class, LooprWalletModule::class])
interface CoreLooprComponent {

    // Applications

    fun inject(application: CoreLooprWalletApp)

    // Activities

    fun inject(baseActivity: BaseActivity)
    fun inject(settingsActivity: SettingsActivity)

    // Dialogs

    fun inject(baseBottomSheetDialog: BaseBottomSheetDialog)

    // Fragments

    fun inject(baseFragment: BaseFragment)
    fun inject(homeSettingsFragment: HomeSettingsFragment)
    fun inject(baseSecurityFragment: BaseSecurityFragment)
    fun inject(securitySettingsFragment: SecuritySettingsFragment)
    fun inject(ethereumFeeSettingsFragment: EthereumFeeSettingsFragment)
    fun inject(loopringFeeSettingsFragment: LoopringFeeSettingsFragment)

    // Services

    fun inject(ethereumService: EthereumService)

    // Other

    fun inject(tokenNumberPadPresenter: TokenNumberPadPresenter)

    // Values - Settings
    val userWalletSettings: UserWalletSettings
    val themeSettings: ThemeSettings
    val currencySettings: CurrencySettings
    val securitySettings: SecuritySettings
    val ethereumFeeSettings: EthereumFeeSettings
    val loopringFeeSettings: LoopringFeeSettings
    val loopringNetworkSettings: LoopringNetworkSettings
    val generalWalletSettings: GeneralWalletSettings
    val userPinSettings: UserPinSettings

    // Values - Clients

    val realmClient: RealmClient
    val walletClient: WalletClient
    val web3j: Web3j
}

interface CoreLooprComponentProvider {

    fun provideCoreLooprComponent(): CoreLooprComponent
}

internal val coreLooprComponent: CoreLooprComponent
    get() = (CoreLooprWalletApp.application as CoreLooprComponentProvider).provideCoreLooprComponent()