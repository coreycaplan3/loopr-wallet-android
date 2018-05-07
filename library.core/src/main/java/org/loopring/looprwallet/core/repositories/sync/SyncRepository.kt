package org.loopring.looprwallet.core.repositories.sync

import io.realm.kotlin.where
import kotlinx.coroutines.experimental.android.HandlerContext
import kotlinx.coroutines.experimental.android.UI
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.repositories.BaseRealmRepository
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
class SyncRepository : BaseRealmRepository(false) {

    /**
     * Gets the last sync time for a given [SyncData.SyncType].
     */
    fun getLastSyncTime(@SyncData.SyncType syncType: String, context: HandlerContext = UI): Date? {
        return getRealmFromContext(context)
                .where<SyncData>()
                .equalTo(SyncData::syncType, syncType)
                .findFirst()
                ?.lastSyncTime
    }

    /**
     * Gets the last sync time for a given syncId's [SyncData.SyncType].
     */
    fun getLastSyncTimeForSyncId(syncType: String, syncId: String, context: HandlerContext = UI): Date? {
        return getRealmFromContext(context)
                .where<SyncData>()
                .equalTo(SyncData::syncType, syncType)
                .equalTo(SyncData::syncId, syncId)
                .findFirst()
                ?.lastSyncTime
    }

}