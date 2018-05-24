package org.loopring.looprwallet.core.networking.loopr

import io.realm.RealmList
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.models.android.architecture.NET
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairGraphFilter
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairTrend
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.repositories.BaseRealmRepository
import org.loopring.looprwallet.core.repositories.loopr.LooprMarketsRepository
import org.loopring.looprwalletnetwork.models.loopring.responseObjects.LooprSupportedToken
import org.loopring.looprwalletnetwork.services.LoopringService
import java.math.BigInteger

/**
 * Created by corey on 5/9/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class LooprMarketsServiceProdImpl : LooprMarketsService {

    private val service by lazy {
        LoopringService()
    }

    override fun getMarkets(): Deferred<RealmList<TradingPair>> = async(NET) {
        val tickerList = service.getTicker().await().tickers
                ?: throw IllegalStateException("Tickers was null!")

        val repository = LooprMarketsRepository()
        val list = tickerList.mapNotNull { ticker ->
            val market = ticker.market ?: return@mapNotNull null

            return@mapNotNull repository.getMarketNow(market)?.apply {
                this.highPrice = ticker.high ?: return@apply
                this.lowPrice = ticker.low ?: return@apply
                this.lastPrice = ticker.last ?: return@apply
                this.change24h = ticker.change ?: return@apply
                this.amountOfPrimary = ticker.amount ?: return@apply
                this.volumeOfSecondary = ticker.vol ?: return@apply
            }
        }

        return@async RealmList<TradingPair>().apply {
            this.addAll(list)
        }
    }

    override fun syncSupportedMarkets(): Deferred<Unit> = async(NET) {
        val repository = BaseRealmRepository()

        // JOBS
        val tokensJob = service.getSupportedTokens()
        val marketsJob = service.getSupportedMarket()

        val supportedTokens = tokensJob.await().tokens

        // TOKENS
        val mappedTokens = supportedTokens?.mapNotNull {
            val token = mapSupportedTokenToLooprToken(it)
            if (token != null) {
                repository.add(token, NET)
            }
            return@mapNotNull token
        }?.sortedBy { it.ticker } ?: return@async

        // MARKETS
        val secondaryTokenCache = mutableListOf<LooprToken>()
        val tradingPairs = marketsJob.await().pairs?.mapNotNull { market ->
            val splitMarket = market.split(Regex("-+"))

            if (splitMarket.size != 2) {
                return@mapNotNull null
            }

            val primaryToken = getTokenFromList(mappedTokens, splitMarket[0])
                    ?: return@mapNotNull null

            val secondaryToken = getTokenFromList(mappedTokens, splitMarket[1], secondaryTokenCache)
                    ?: return@mapNotNull null

            return@mapNotNull TradingPair(market).apply {
                this.primaryToken = primaryToken
                this.secondaryToken = secondaryToken
            }
        }

        repository.addList(mappedTokens, NET)
        tradingPairs?.let { repository.addList(it, NET) }
    }

    override fun getMarketDetails(tradingPairMarket: String): Deferred<TradingPair> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMarketTrends(tradingPairGraphFilter: TradingPairGraphFilter): Deferred<RealmList<TradingPairTrend>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // MARK - Private Methods

    private fun mapSupportedTokenToLooprToken(token: LooprSupportedToken): LooprToken? {
        val identifier = token.protocol ?: return null
        val ticker = token.symbol ?: return null
        val name = token.symbol ?: return null
        val decimalPlacesBigInteger = token.decimals ?: return null
        val totalSupply = BigInteger.ONE

        var decimalPlaces = 18
        if (BigInteger.TEN.pow(18) / decimalPlacesBigInteger == BigInteger.ONE) {
            // 18 is the most common, we'll try that first
            decimalPlaces = 18
        } else {
            for (i in 30 downTo 0) {
                if (BigInteger.TEN.pow(i) / decimalPlacesBigInteger == BigInteger.ONE) {
                    decimalPlaces = i
                    break
                }
            }
        }

        return LooprToken(identifier, ticker, name, totalSupply, decimalPlaces)
    }

    private fun getTokenFromList(allTokens: List<LooprToken>, tickerToFind: String, cache: MutableList<LooprToken>? = null): LooprToken? {
        cache?.find { it.ticker == tickerToFind }
                ?.let { token ->
                    return token
                }

        val index = allTokens.binarySearch { it.ticker.compareTo(tickerToFind) }

        return when {
            index >= 0 -> {
                val token = allTokens[index]
                cache?.add(token)
                token
            }
            else -> null
        }
    }

}