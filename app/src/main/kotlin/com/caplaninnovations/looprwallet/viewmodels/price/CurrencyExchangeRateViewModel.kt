package com.caplaninnovations.looprwallet.viewmodels.price

import android.arch.lifecycle.LiveData
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.models.currency.CurrencyExchangeRate
import com.caplaninnovations.looprwallet.models.wallet.LooprWallet
import com.caplaninnovations.looprwallet.networking.prices.CurrencyExchangeService
import com.caplaninnovations.looprwallet.repositories.currency.CurrencyExchangeRateRepository
import com.caplaninnovations.looprwallet.viewmodels.StreamingViewModel
import kotlinx.coroutines.experimental.Deferred

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

    companion object {
        const val TEN_MINUTES_MILLIS = 1000L * 60L * 10L
    }

    private val currencyExchangeService = CurrencyExchangeService.getInstance()

    override val pingTime = TEN_MINUTES_MILLIS

    private val currencySettings = LooprWalletApp.dagger.currencySettings

    fun start(onChange: (CurrencyExchangeRate) -> Unit) {
        initializeDataForever(currencySettings.getCurrentCurrency(), onChange)
    }

    override fun getLiveDataFromRepository(parameter: String): LiveData<CurrencyExchangeRate> {
        return repository.getCurrencyExchangeRate(parameter)
    }

    override fun getDataFromNetwork(parameter: String): Deferred<CurrencyExchangeRate> {
        return currencyExchangeService.getCurrentCurrencyExchangeRate(parameter)
    }

    override fun isRefreshNecessary(): Boolean {
        val dateLastUpdated = data?.lastUpdated?.time ?: return true

        return isDefaultRefreshNecessary(dateLastUpdated, pingTime)
    }

    override fun addNetworkDataToRepository(data: CurrencyExchangeRate) {
        repository.add(data)
    }

}