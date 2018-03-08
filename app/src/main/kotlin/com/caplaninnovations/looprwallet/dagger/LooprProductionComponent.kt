package com.caplaninnovations.looprwallet.dagger

import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.models.android.settings.ThemeSettings
import com.caplaninnovations.looprwallet.models.android.settings.WalletSettings
import com.caplaninnovations.looprwallet.models.security.WalletClient
import com.caplaninnovations.looprwallet.realm.RealmClient
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
    LooprSecurityModule::class, LooprEthModule::class])
interface LooprProductionComponent {

    fun inject(baseActivity: BaseActivity)
    fun inject(looprWalletApp: LooprWalletApp)

    val walletSettings: WalletSettings
    val themeSettings: ThemeSettings
    val realmClient: RealmClient
    val walletClient: WalletClient
    val web3j: Web3j
}