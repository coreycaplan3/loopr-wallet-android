package org.loopring.looprwallet.core.viewmodels.price

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import io.realm.RealmList
import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.extensions.insertOrUpdate
import org.loopring.looprwallet.core.fragments.ViewLifecycleFragment
import org.loopring.looprwallet.core.models.android.architecture.NET
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.loopr.tokens.TokenPriceInfo
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.networking.loopr.LooprPriceService
import org.loopring.looprwallet.core.repositories.eth.EthTokenRepository
import org.loopring.looprwallet.core.viewmodels.StreamingViewModel
import org.loopring.looprwalletnetwork.models.loopring.responseObjects.LooprTokenPriceQuote
import java.math.BigDecimal
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
class LooprTokenPriceCheckerViewModel : StreamingViewModel<OrderedRealmCollection<LooprToken>, String>() {

    override val repository = EthTokenRepository()

    override val syncType: String = SyncData.SYNC_TYPE_TOKEN_PRICE

    private val service by lazy {
        LooprPriceService.getInstance()
    }

    /**
     * Gets the current price of the provided ticker by hitting the database first, followed by
     * the network. This method kicks off a "stream" event that will forward price changes every
     *
     * @param owner The [LifecycleOwner] that will be observing this ViewModel
     * @param currency The currency that the user would like to view the price in
     * @param onChange A function that is called whenever the [LooprToken] changes to a valid
     * value.
     */
    fun getTokenPrice(owner: ViewLifecycleFragment,
                      currency: String,
                      onChange: (OrderedRealmCollection<LooprToken>) -> Unit
    ) {
        initializeData(owner, currency, onChange)
    }

    override fun getLiveDataFromRepository(parameter: String): LiveData<OrderedRealmCollection<LooprToken>> {
        return repository.getAllTokens()
    }

    override fun getDataFromNetwork(parameter: String) = async(NET) {
        val priceQuoteList = service.getPriceQuote(parameter).await().tokens
                ?: throw IllegalStateException("Could not load tokens!")

        mapTokenListToRealmList(parameter, priceQuoteList)
    }

    private fun mapTokenListToRealmList(currency: String, priceList: List<LooprTokenPriceQuote>): RealmList<LooprToken> {
        val scale = 4
        val power = BigDecimal(10000)

        val mappedList = priceList.mapNotNull {
            val ticker = it.symbol ?: return@mapNotNull null
            val priceInfo = it.price?.setScale(scale, RoundingMode.HALF_UP)
                    ?: return@mapNotNull null
            val priceInfoBigInteger = (priceInfo * power).toBigInteger()

            val tokenPriceInfo = TokenPriceInfo(currency).apply { this.price = priceInfoBigInteger }

            val token = repository.getTokenByTickerNow(ticker, NET) ?: return@mapNotNull null
            token.tokenPrices.insertOrUpdate(tokenPriceInfo) {
                it.currency == currency
            }
            token
        }

        return RealmList<LooprToken>().apply { addAll(mappedList) }
    }

    override fun addNetworkDataToRepository(data: OrderedRealmCollection<LooprToken>, parameter: String) {
        repository.addList(data)
    }

    override fun isRefreshNecessary(parameter: String) = defaultIsRefreshNecessary(parameter)

    override fun addSyncDataToRepository(parameter: String) {
        syncRepository.add(SyncData(syncType, parameter, Date()))
    }

}