package com.caplaninnovations.looprwallet.dagger

import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.models.android.settings.ThemeSettings
import com.caplaninnovations.looprwallet.models.android.settings.WalletSettings
import com.caplaninnovations.looprwallet.models.security.SecurityClient
import com.caplaninnovations.looprwallet.realm.RealmClient
import com.caplaninnovations.looprwallet.viewmodels.EthereumTransactionViewModel
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
@Component(modules = [LooprSettingsModule::class, LooprRealmModule::class,
    LooprSecurityModule::class])
interface LooprProductionComponent {

    fun inject(baseActivity: BaseActivity)
    fun inject(looprWalletApp: LooprWalletApp)

    val walletSettings: WalletSettings
    val themeSettings: ThemeSettings
    val realmClient: RealmClient
    val securityClient: SecurityClient
    val web3j: Web3j
}