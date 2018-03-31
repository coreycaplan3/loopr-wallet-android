package com.caplaninnovations.looprwallet.repositories.sync

import com.caplaninnovations.looprwallet.models.user.SyncData
import com.caplaninnovations.looprwallet.models.wallet.LooprWallet
import com.caplaninnovations.looprwallet.repositories.BaseRepository
import io.realm.RealmModel
import java.util.*

/**
 * Created by Corey on 3/22/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
interface SyncRepository : BaseRepository<RealmModel> {

    companion object {

        fun getInstance(currentWallet: LooprWallet): SyncRepository = SyncRepositoryImpl(currentWallet)

    }

    /**
     * Gets the last sync time for a given [SyncData.SyncType].
     */
    fun getLastSyncTime(@SyncData.SyncType syncType: String): Date?

    /**
     * Gets the last sync time for a given address's [SyncData.SyncType].
     */
    fun getLastSyncTimeForWallet(address: String, @SyncData.SyncType syncType: String): Date?

}