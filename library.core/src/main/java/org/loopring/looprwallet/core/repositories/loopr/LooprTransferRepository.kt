package org.loopring.looprwallet.core.repositories.loopr

import android.arch.lifecycle.LiveData
import io.realm.*
import io.realm.kotlin.where
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.extensions.sort
import org.loopring.looprwallet.core.models.cryptotokens.LooprToken
import org.loopring.looprwallet.core.models.transfers.LooprTransfer
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.repositories.BaseRealmRepository

/**
 * Created by Corey on 4/20/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class LooprTransferRepository(private val currentWallet: LooprWallet) : BaseRealmRepository() {

    override fun getRealm() = realmClient.getPrivateInstance(currentWallet)

    fun getAllTransfers(): LiveData<OrderedRealmCollection<LooprTransfer>> {
        // We're in a private realm instance, so we're already querying by all of this address's
        // orders
        return uiRealm.where<LooprTransfer>()
                .sort(LooprTransfer::timestamp, Sort.DESCENDING)
                .findAllAsync()
                .asLiveData()
    }

    fun getAllTransfersByToken(identifier: String): LiveData<OrderedRealmCollection<LooprTransfer>> {
        // We're in a private realm instance, so we're already querying by all of this address's
        // orders. We just need to filter by the token's identifier instead.
        return uiRealm.where<LooprTransfer>()
                .equalTo(listOf(LooprTransfer::token), LooprToken::identifier, identifier)
                .sort(LooprTransfer::timestamp, Sort.DESCENDING)
                .findAllAsync()
                .asLiveData()
    }

}