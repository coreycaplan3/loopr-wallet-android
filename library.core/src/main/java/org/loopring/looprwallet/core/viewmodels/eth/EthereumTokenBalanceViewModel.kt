package org.loopring.looprwallet.core.viewmodels.eth

import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.extensions.upsert
import org.loopring.looprwallet.core.fragments.ViewLifecycleFragment
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.networking.ethplorer.LooprEthplorerService
import org.loopring.looprwallet.core.repositories.eth.EthTokenRepository
import org.loopring.looprwallet.core.utilities.LooprTokenUtility
import org.loopring.looprwallet.core.viewmodels.OfflineFirstViewModel
import java.util.*

/**
 * Created by Corey Caplan on 3/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: Retrieves the balance and allowance information for all tokens the user has
 * in their wallet.
 */
class EthereumTokenBalanceViewModel : OfflineFirstViewModel<OrderedRealmCollection<LooprToken>, String>() {

    override val syncType = SyncData.SYNC_TYPE_TOKEN_BALANCE

    override val repository = EthTokenRepository()

    private val ethplorerService by lazy {
        LooprEthplorerService.getInstance()
    }

    fun getEthBalanceNow() = repository.getEthNow()

    fun getAllTokensWithBalances(
            owner: ViewLifecycleFragment,
            address: String,
            onChange: (OrderedRealmCollection<LooprToken>) -> Unit
    ) {
        initializeData(owner, address, onChange)
    }

    override fun getLiveDataFromRepository(parameter: String): LiveData<OrderedRealmCollection<LooprToken>> {
        return repository.getAllTokens()
    }

    override fun isRefreshNecessary(parameter: String) = defaultIsRefreshNecessary(parameter)

    override fun getDataFromNetwork(parameter: String): Deferred<OrderedRealmCollection<LooprToken>> {
        // TODO add allowance information
        return ethplorerService.getAddressBalances(parameter)
    }

    override fun addNetworkDataToRepository(data: OrderedRealmCollection<LooprToken>, parameter: String) {
        val address = mParameter ?: return

        repository.runTransaction { realm ->
            data.map { mapTokenToExistingOneIfPossible(it, address) }
                    .forEach(realm::upsert)
        }
    }

    override fun addSyncDataToRepository(parameter: String) {
        syncRepository.add(SyncData(syncType, parameter, Date()))
    }

    // MARK - Private Methods

    /**
     * Returns the given [newToken] if it doesn't already exist in realm. If it does exist, it's
     * information is retrieved from the realm and its fields are updated with the new ones.
     *
     * @return A [LooprToken] that can be safely upserted into the realm
     */
    private fun mapTokenToExistingOneIfPossible(newToken: LooprToken, address: String): LooprToken {
        val oldToken = repository.getTokenByContractAddressNow(newToken.identifier, IO)

        return LooprTokenUtility.mapTokenToBalanceAndReturnToken(newToken, oldToken, address)
    }

}