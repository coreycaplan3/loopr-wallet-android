package org.loopring.looprwallet.core.viewmodels.price

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import io.realm.RealmModel
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.extensions.logw
import org.loopring.looprwallet.core.fragments.ViewLifecycleFragment
import org.loopring.looprwallet.core.models.currency.CurrencyExchangeRate
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.networking.ethplorer.LooprEthplorerService
import org.loopring.looprwallet.core.repositories.eth.EthTokenRepository
import org.loopring.looprwallet.core.viewmodels.StreamingViewModel
import java.math.BigDecimal
import java.math.BigInteger
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
class EthTokenPriceCheckerViewModel : StreamingViewModel<LooprToken, String>() {

    override val repository = EthTokenRepository()

    override val syncType: String = SyncData.SYNC_TYPE_TOKEN_PRICE

    private var currencyExchangeRate: CurrencyExchangeRate? = null

    private val currencyExchangeRateViewModel = CurrencyExchangeRateViewModel().apply {
        this.start { currencyExchangeRate = it }
    }

    var currentCryptoToken: LooprToken? = null
        private set

    private val ethplorerTokenApiService = LooprEthplorerService.getInstance()

    /**
     * Gets the current price of the provided ticker by hitting the database first, followed by
     * the network. This method kicks off a "stream" event that will forward price changes every
     *
     * @param owner The [LifecycleOwner] that will be observing this ViewModel
     * @param identifier The identifier that's used to get the [LooprToken] from the repository
     * and network. For Ethereum, this is the contract's address.
     * @param onChange A function that is called whenever the [LooprToken] changes to a valid
     * value.
     */
    fun getTokenPrice(owner: ViewLifecycleFragment,
                      identifier: String,
                      onChange: (LooprToken) -> Unit
    ) {
        val interceptedChange = interceptOnChange(onChange)
        initializeData(owner, identifier, interceptedChange)
    }

    override fun getLiveDataFromRepository(parameter: String): LiveData<LooprToken> {
        return repository.getToken(parameter)
    }

    override fun getDataFromNetwork(parameter: String): Deferred<LooprToken> {
        return ethplorerTokenApiService.getTokenInfo(parameter)
    }

    override fun addNetworkDataToRepository(data: LooprToken, parameter: String) {
        (data as? RealmModel)?.let { repository.add(it) }
    }

    override fun isRefreshNecessary(parameter: String) = defaultIsRefreshNecessary(parameter)

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
     * @return A new function that takes [LooprToken] as a parameter and returns nothing.
     */
    private fun interceptOnChange(onChange: (LooprToken) -> Unit) = function@{ token: LooprToken ->
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

        val bdPriceInUsd = BigDecimal(priceInUsd) / BigDecimal("100")
        val priceInNativeCurrency = (bdPriceInUsd * rateAgainstUsd)
                .setScale(8, RoundingMode.HALF_EVEN)

        val x = (priceInNativeCurrency * BigDecimal(100))
                .setScale(0, RoundingMode.HALF_EVEN)
                .toPlainString()
        token.priceInNativeCurrency = BigInteger(x)

        onChange(token)
    }

}