package org.loopring.looprwallet.core.viewmodels.price

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import io.realm.RealmModel
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.cryptotokens.EthToken
import org.loopring.looprwallet.core.extensions.logw
import org.loopring.looprwallet.core.models.currency.CurrencyExchangeRate
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.networking.ethplorer.EthplorerService
import org.loopring.looprwallet.core.repositories.eth.EthTokenRepository
import org.loopring.looprwallet.core.viewmodels.StreamingViewModel
import java.math.RoundingMode
import java.util.*

/**
 * Created by Corey Caplan on 3/13/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A [StreamingViewModel] that periodically checks the token price and sends the
 * result via a channel to a *LiveData* instance.
 */
class EthTokenPriceCheckerViewModel : StreamingViewModel<EthToken, String>() {

    override val repository = EthTokenRepository()

    override val syncType: String = SyncData.SYNC_TYPE_TOKEN_PRICE

    private var currencyExchangeRate: CurrencyExchangeRate? = null

    private val currencyExchangeRateViewModel = CurrencyExchangeRateViewModel().apply {
        this.start { currencyExchangeRate = it }
    }

    var currentCryptoToken: EthToken? = null
        private set

    private val ethplorerTokenApiService = EthplorerService.getInstance()

    /**
     * Gets the current price of the provided ticker by hitting the database first, followed by
     * the network. This method kicks off a "stream" event that will forward price changes every
     *
     * @param owner The [LifecycleOwner] that will be observing this ViewModel
     * @param identifier The identifier that's used to get the [EthToken] from the repository
     * and network. For Ethereum, this is the contract's address.
     * @param onChange A function that is called whenever the [EthToken] changes to a valid
     * value.
     */
    fun getTokenPrice(owner: LifecycleOwner,
                      identifier: String,
                      onChange: (EthToken) -> Unit
    ) {
        val interceptedChange = interceptOnChange(onChange)
        initializeData(owner, identifier, interceptedChange)
    }

    override fun getLiveDataFromRepository(parameter: String): LiveData<EthToken> {
        return repository.getToken(parameter)
    }

    override fun getDataFromNetwork(parameter: String): Deferred<EthToken> {
        return ethplorerTokenApiService.getTokenInfo(parameter)
    }

    override fun addNetworkDataToRepository(data: EthToken) {
        (data as? RealmModel)?.let { repository.add(it) }
    }

    override fun isRefreshNecessary(parameter: String) = isRefreshNecessaryDefault(parameter)

    override fun addSyncDataToRepository(parameter: String) {
        syncRepository.add(SyncData(syncType, parameter, Date()))
    }

    override fun onCleared() {
        super.onCleared()

        currencyExchangeRateViewModel.clear()
    }

    // MARK - Private Methods

    /**
     * Intercepts the supplied [onChange] method to multiply the token price by the current
     * [currencyExchangeRate].
     *
     * @return A new function that takes [EthToken] as a parameter and returns nothing.
     */
    private fun interceptOnChange(onChange: (EthToken) -> Unit) = function@{ token: EthToken ->
        currentCryptoToken = token

        val rateAgainstUsd = currencyExchangeRate?.rateAgainstToUsd
        if (rateAgainstUsd == null) {
            logw("rateAgainstToUsd has not loaded yet or we are in an invalid state!",
                    IllegalStateException())
            onChange(token)
            return@function
        }

        val priceInUsd = token.priceInUsd
        if (priceInUsd == null) {
            logw("priceInUsd has not loaded yet or we are in an invalid state!",
                    IllegalStateException())
            onChange(token)
            return@function
        }

        val priceInNativeCurrency = (priceInUsd * rateAgainstUsd)
                .setScale(8, RoundingMode.HALF_EVEN)

        token.priceInNativeCurrency = priceInNativeCurrency

        onChange(token)
    }

}