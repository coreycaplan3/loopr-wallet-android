package org.loopring.looprwallet.application

import com.google.firebase.FirebaseApp
import com.google.firebase.crash.FirebaseCrash
import org.loopring.looprwallet.core.BuildConfig
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.createorder.dagger.CreateOrderLooprComponent
import org.loopring.looprwallet.createorder.dagger.CreateOrderLooprComponentProvider
import org.loopring.looprwallet.createorder.dagger.DaggerCreateOrderLooprComponent
import org.loopring.looprwallet.createtransfer.dagger.CreateTransferLooprComponent
import org.loopring.looprwallet.createtransfer.dagger.CreateTransferLooprComponentProvider
import org.loopring.looprwallet.createtransfer.dagger.DaggerCreateTransferLooprComponent
import org.loopring.looprwallet.home.activities.MainActivity
import org.loopring.looprwallet.homemarkets.dagger.DaggerHomeMarketsLooprComponent
import org.loopring.looprwallet.homemarkets.dagger.HomeMarketsLooprComponent
import org.loopring.looprwallet.homemarkets.dagger.HomeMarketsLooprComponentProvider
import org.loopring.looprwallet.homemywallet.dagger.DaggerHomeMyWalletLooprComponent
import org.loopring.looprwallet.homemywallet.dagger.HomeMyWalletLooprComponent
import org.loopring.looprwallet.homemywallet.dagger.HomeMyWalletLooprComponentProvider
import org.loopring.looprwallet.homeorders.dagger.DaggerHomeOrdersLooprComponent
import org.loopring.looprwallet.homeorders.dagger.HomeOrdersLooprComponent
import org.loopring.looprwallet.homeorders.dagger.HomeOrdersLooprComponentProvider
import org.loopring.looprwallet.hometransfers.dagger.DaggerHomeTransfersLooprComponent
import org.loopring.looprwallet.hometransfers.dagger.HomeTransfersLooprComponent
import org.loopring.looprwallet.hometransfers.dagger.HomeTransfersLooprComponentProvider
import org.loopring.looprwallet.orderdetails.dagger.DaggerOrderDetailsLooprComponent
import org.loopring.looprwallet.orderdetails.dagger.OrderDetailsLooprComponent
import org.loopring.looprwallet.orderdetails.dagger.OrderDetailsLooprComponentProvider
import org.loopring.looprwallet.tradedetails.dagger.DaggerTradeDetailsLooprComponent
import org.loopring.looprwallet.tradedetails.dagger.TradeDetailsLooprComponent
import org.loopring.looprwallet.tradedetails.dagger.TradeDetailsLooprComponentProvider
import org.loopring.looprwallet.transferdetails.dagger.DaggerTransferDetailsLooprComponent
import org.loopring.looprwallet.transferdetails.dagger.TransferDetailsLooprComponent
import org.loopring.looprwallet.transferdetails.dagger.TransferDetailsLooprComponentProvider
import org.loopring.looprwallet.viewbalances.dagger.DaggerViewBalancesLooprComponent
import org.loopring.looprwallet.viewbalances.dagger.ViewBalancesLooprComponent
import org.loopring.looprwallet.viewbalances.dagger.ViewBalancesLooprComponentProvider
import org.loopring.looprwallet.walletsignin.activities.SignInActivity
import org.loopring.looprwallet.walletsignin.dagger.DaggerWalletLooprComponent
import org.loopring.looprwallet.walletsignin.dagger.WalletLooprComponent
import org.loopring.looprwallet.walletsignin.dagger.WalletLooprComponentProvider
import org.loopring.looprwallet.wrapeth.dagger.DaggerWrapEthLooprComponent
import org.loopring.looprwallet.wrapeth.dagger.WrapEthLooprComponent
import org.loopring.looprwallet.wrapeth.dagger.WrapEthLooprComponentProvider

/**
 * Created by Corey on 1/18/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
open class LooprWalletApp : CoreLooprWalletApp(), CreateOrderLooprComponentProvider,
        CreateTransferLooprComponentProvider, HomeMarketsLooprComponentProvider,
        HomeMyWalletLooprComponentProvider, HomeOrdersLooprComponentProvider,
        HomeTransfersLooprComponentProvider, OrderDetailsLooprComponentProvider,
        TradeDetailsLooprComponentProvider, TransferDetailsLooprComponentProvider,
        ViewBalancesLooprComponentProvider, WalletLooprComponentProvider,
        WrapEthLooprComponentProvider {

    companion object {

            val createOrderLooprComponent: CreateOrderLooprComponent by lazy {
                DaggerCreateOrderLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
            }

        val createTransferLooprComponent: CreateTransferLooprComponent by lazy {
            DaggerCreateTransferLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
        }

        val homeMarketsLooprComponent: HomeMarketsLooprComponent by lazy {
            DaggerHomeMarketsLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
        }

        val homeMyWalletLooprComponent: HomeMyWalletLooprComponent by lazy {
            DaggerHomeMyWalletLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
        }

        val homeOrdersLooprComponent: HomeOrdersLooprComponent by lazy {
            DaggerHomeOrdersLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
        }

        val homeTransfersLooprComponent: HomeTransfersLooprComponent by lazy {
            DaggerHomeTransfersLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
        }

        val orderDetailsLooprComponent: OrderDetailsLooprComponent by lazy {
            DaggerOrderDetailsLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
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

        val wrapEthLooprComponent: WrapEthLooprComponent by lazy {
            DaggerWrapEthLooprComponent.builder().coreLooprComponent(coreLooprComponent).build()
        }

    }

    override fun provideCreateOrderLooprComponent() = createOrderLooprComponent

    override fun provideCreateTransferLooprComponent() = createTransferLooprComponent

    override fun provideHomeMarketsLooprComponent() = homeMarketsLooprComponent

    override fun provideHomeMyWalletLooprComponent() = homeMyWalletLooprComponent

    override fun provideHomeOrdersLooprComponent() = homeOrdersLooprComponent

    override fun provideHomeTransfersLooprComponent() = homeTransfersLooprComponent

    override fun provideOrderDetailsLooprComponent() = orderDetailsLooprComponent

    override fun provideTradeDetailsLooprComponent() = tradeDetailsLooprComponent

    override fun provideTransferDetailsLooprComponent() = transferDetailsLooprComponent

    override fun provideViewBalancesLooprComponent() = viewBalancesLooprComponent

    override fun provideWalletLooprComponent() = walletLooprComponent

    override fun provideWrapEthLooprComponent() = wrapEthLooprComponent

    override fun onCreate() {
        super.onCreate()

        CoreLooprWalletApp.mainClass = MainActivity::class.java
        CoreLooprWalletApp.signInClass = SignInActivity::class.java

        FirebaseApp.initializeApp(this)
        FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG)
    }

}