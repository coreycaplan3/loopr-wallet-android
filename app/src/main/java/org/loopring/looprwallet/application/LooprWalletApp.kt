package org.loopring.looprwallet.application

import com.google.firebase.FirebaseApp
import com.google.firebase.crash.FirebaseCrash
import org.loopring.looprwallet.core.BuildConfig
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.home.activities.MainActivity
import org.loopring.looprwallet.createtransfer.dagger.CreateTransferLooprComponent
import org.loopring.looprwallet.createtransfer.dagger.DaggerCreateTransferLooprComponent
import org.loopring.looprwallet.createtransfer.dagger.CreateTransferLooprComponentProvider
import org.loopring.looprwallet.homemywallet.dagger.DaggerHomeMyWalletLooprComponent
import org.loopring.looprwallet.homemywallet.dagger.HomeMyWalletLooprComponent
import org.loopring.looprwallet.homemywallet.dagger.HomeMyWalletLooprComponentProvider
import org.loopring.looprwallet.tradedetails.dagger.DaggerTradeDetailsLooprComponent
import org.loopring.looprwallet.tradedetails.dagger.TradeDetailsLooprComponent
import org.loopring.looprwallet.tradedetails.dagger.TradeDetailsLooprComponentProvider
import org.loopring.looprwallet.transferdetails.dagger.DaggerTransferDetailsLooprComponent
import org.loopring.looprwallet.transferdetails.dagger.TransferDetailsLooprComponent
import org.loopring.looprwallet.transferdetails.dagger.TransferDetailsLooprComponentProvider
import org.loopring.looprwallet.viewbalances.dagger.DaggerViewBalancesLooprComponent
import org.loopring.looprwallet.viewbalances.dagger.ViewBalancesLooprComponent
import org.loopring.looprwallet.viewbalances.dagger.ViewBalancesLooprComponentProvider
import org.loopring.looprwallet.walletsignin.dagger.DaggerWalletLooprComponent
import org.loopring.looprwallet.walletsignin.dagger.WalletLooprComponent
import org.loopring.looprwallet.walletsignin.dagger.WalletLooprComponentProvider

/**
 * Created by Corey on 1/18/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
open class LooprWalletApp : CoreLooprWalletApp(), CreateTransferLooprComponentProvider,
        HomeMyWalletLooprComponentProvider, TradeDetailsLooprComponentProvider,
        TransferDetailsLooprComponentProvider, ViewBalancesLooprComponentProvider,
        WalletLooprComponentProvider {

    companion object {

        val createTransferLooprComponent: CreateTransferLooprComponent by lazy {
            DaggerCreateTransferLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
        }

        val homeMyWalletLooprComponent: HomeMyWalletLooprComponent by lazy {
            DaggerHomeMyWalletLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
        }

        val tradeDetailsLooprComponent: TradeDetailsLooprComponent by lazy {
            DaggerTradeDetailsLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
        }

        val transferDetailsLooprComponent: TransferDetailsLooprComponent by lazy {
            DaggerTransferDetailsLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
        }

        val viewBalancesLooprComponent: ViewBalancesLooprComponent by lazy {
            DaggerViewBalancesLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
        }

        val walletLooprComponent: WalletLooprComponent by lazy {
            DaggerWalletLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
        }

    }

    override fun provideHomeMyWalletLooprComponent() = homeMyWalletLooprComponent

    override fun provideCreateTransferLooprComponent() = createTransferLooprComponent

    override fun provideTradeDetailsLooprComponent() = tradeDetailsLooprComponent

    override fun provideTransferDetailsLooprComponent() = transferDetailsLooprComponent

    override fun provideViewBalancesLooprComponent() = viewBalancesLooprComponent

    override fun provideWalletLooprComponent() = walletLooprComponent

    override fun onCreate() {
        super.onCreate()

        CoreLooprWalletApp.mainClass = MainActivity::class.java

        FirebaseApp.initializeApp(this)
        FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG)
    }

}