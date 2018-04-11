package org.loopring.looprwallet.core.viewmodels.price

import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.dagger.coreLooprComponent
import org.loopring.looprwallet.core.models.currency.CurrencyExchangeRate
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.networking.prices.CurrencyExchangeService
import org.loopring.looprwallet.core.repositories.currency.CurrencyExchangeRateRepository
import org.loopring.looprwallet.core.viewmodels.StreamingViewModel
import java.util.*
import javax.inject.Inject

/**
 * Created by Corey Caplan on 3/15/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To monitor changes in foreign exchange rates between fiat currencies.
 *
 */
class CurrencyExchangeRateViewModel() : StreamingViewModel<CurrencyExchangeRate, String>() {

    override val repository = CurrencyExchangeRateRepository()

    override val syncType = SyncData.SYNC_TYPE_CURRENCY_EXCHANGE_RATE

    companion object {
        const val TEN_MINUTES_MILLIS = 1000L * 60L * 10L
    }

    private val currencyExchangeService = CurrencyExchangeService.getInstance()

    override val waitTime: Long = TEN_MINUTES_MILLIS

    @Inject
    lateinit var currencySettings: CurrencySettings

    init {
        coreLooprComponent.inject(this)
    }

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

    override fun isRefreshNecessary(parameter: String) = isRefreshNecessaryDefault(parameter)

    override fun addSyncDataToRepository(parameter: String) {
        syncRepository.add(SyncData(syncType, parameter, Date()))
    }

}