package org.loopring.looprwallet.core.viewmodels.price

import android.arch.lifecycle.LiveData
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.repositories.sync.SyncRepository
import org.loopring.looprwallet.core.viewmodels.StreamingViewModel
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.application.LooprWalletCoreApp
import org.loopring.looprwallet.core.models.currency.CurrencyExchangeRate
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.networking.prices.CurrencyExchangeService
import org.loopring.looprwallet.core.repositories.currency.CurrencyExchangeRateRepository

/**
 * Created by Corey Caplan on 3/15/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To monitor changes in foreign exchange rates between fiat currencies.
 *
 */
class CurrencyExchangeRateViewModel(currentWallet: LooprWallet) : StreamingViewModel<CurrencyExchangeRate, String>() {

    override val repository = CurrencyExchangeRateRepository(currentWallet)

    override val syncRepository = SyncRepository.getInstance(currentWallet)

    override val syncType = SyncData.SYNC_TYPE_CURRENCY_EXCHANGE_RATE

    companion object {
        const val TEN_MINUTES_MILLIS = 1000L * 60L * 10L
    }

    private val currencyExchangeService = CurrencyExchangeService.getInstance()

    override val waitTime: Long = TEN_MINUTES_MILLIS

    private val currencySettings = LooprWalletCoreApp.dagger.currencySettings

    fun start(onChange: (CurrencyExchangeRate) -> Unit) {
        initializeDataForever(currencySettings.getCurrentCurrency(), onChange)
    }

    override fun getLiveDataFromRepository(parameter: String): LiveData<CurrencyExchangeRate> {
        return repository.getCurrencyExchangeRate(parameter)
    }

    override fun getDataFromNetwork(parameter: String): Deferred<CurrencyExchangeRate> {
        return currencyExchangeService.getCurrentCurrencyExchangeRate(parameter)
    }

    override fun addNetworkDataToRepository(data: CurrencyExchangeRate) {
        repository.add(data)
    }

}