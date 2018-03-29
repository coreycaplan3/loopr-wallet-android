package com.caplaninnovations.looprwallet.viewmodels.price

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import com.caplaninnovations.looprwallet.extensions.logw
import com.caplaninnovations.looprwallet.models.crypto.CryptoToken
import com.caplaninnovations.looprwallet.models.currency.CurrencyExchangeRate
import com.caplaninnovations.looprwallet.models.user.SyncData
import com.caplaninnovations.looprwallet.models.wallet.LooprWallet
import com.caplaninnovations.looprwallet.networking.ethplorer.EthplorerApiService
import com.caplaninnovations.looprwallet.repositories.eth.EthTokenRepository
import com.caplaninnovations.looprwallet.repositories.sync.SyncRepository
import com.caplaninnovations.looprwallet.viewmodels.StreamingViewModel
import io.realm.RealmModel
import kotlinx.coroutines.experimental.Deferred
import java.math.RoundingMode

/**
 * Created by Corey Caplan on 3/13/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A [StreamingViewModel] that periodically checks the token price and sends the
 * result via a channel to a *LiveData* instance.
 *
 * @param currentWallet The wallet of the user that is currently signed in.
 */
class TokenPriceCheckerViewModel(currentWallet: LooprWallet) : StreamingViewModel<CryptoToken, String>() {

    override val repository = EthTokenRepository(currentWallet)

    override val syncRepository = SyncRepository.getInstance(currentWallet)

    override val syncType: String = SyncData.SYNC_TYPE_TOKEN_PRICE

    private var currencyExchangeRate: CurrencyExchangeRate? = null

    private val currencyExchangeRateViewModel = CurrencyExchangeRateViewModel(currentWallet)
            .apply {
                this.start { currencyExchangeRate = it }
            }

    var currentCryptoToken: CryptoToken? = null
        private set

    private val ethplorerTokenApiService = EthplorerApiService.getInstance()

    /**
     * Gets the current price of the provided ticker by hitting the database first, followed by
     * the network. This method kicks off a "stream" event that will forward price changes every
     *
     * @param owner The [LifecycleOwner] that will be observing this ViewModel
     * @param identifier The identifier that's used to get the [CryptoToken] from the repository
     * and network. For Ethereum, this is the contract's address.
     * @param onChange A function that is called whenever the [CryptoToken] changes to a valid
     * value.
     */
    fun getTokenPrice(owner: LifecycleOwner,
                      identifier: String,
                      onChange: (CryptoToken) -> Unit
    ) {
        val interceptedChange = interceptOnChange(onChange)
        initializeData(owner, identifier, interceptedChange)
    }

    override fun getLiveDataFromRepository(parameter: String): LiveData<CryptoToken> {
        return repository.getToken(parameter)
    }

    override fun getDataFromNetwork(parameter: String): Deferred<CryptoToken> {
        return ethplorerTokenApiService.getTokenInfo(parameter)
    }

    override fun addNetworkDataToRepository(data: CryptoToken) {
        (data as? RealmModel)?.let { repository.add(it) }
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
     * @return A new function that takes [CryptoToken] as a parameter and returns nothing.
     */
    private fun interceptOnChange(onChange: (CryptoToken) -> Unit): (CryptoToken) -> Unit {
        return inlineFunction@{ token: CryptoToken ->
            currentCryptoToken = token

            val rateAgainstUsd = currencyExchangeRate?.rateAgainstToUsd
            if (rateAgainstUsd == null) {
                logw("rateAgainstToUsd has not loaded yet or we are in an invalid state!",
                        IllegalStateException())
                onChange(token)
                return@inlineFunction
            }

            val priceInUsd = token.priceInUsd
            if (priceInUsd == null) {
                logw("priceInUsd has not loaded yet or we are in an invalid state!",
                        IllegalStateException())
                onChange(token)
                return@inlineFunction
            }

            val priceInNativeCurrency = (priceInUsd * rateAgainstUsd)
                    .setScale(8, RoundingMode.HALF_EVEN)

            token.priceInNativeCurrency = priceInNativeCurrency

            onChange(token)
        }
    }

}