package org.loopring.looprwallet.core.repositories.sync

import io.realm.RealmModel
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.repositories.BaseRepository
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

        fun getInstance(): SyncRepository = SyncRepositoryImpl()

    }

    /**
     * Gets the last sync time for a given [SyncData.SyncType].
     */
    fun getLastSyncTime(@SyncData.SyncType syncType: String): Date?

    /**
     * Gets the last sync time for a given syncId's [SyncData.SyncType].
     */
    fun getLastSyncTimeForSyncId(@SyncData.SyncType syncType: String, syncId: String): Date?

}