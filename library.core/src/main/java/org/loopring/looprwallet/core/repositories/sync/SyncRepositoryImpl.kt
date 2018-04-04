package org.loopring.looprwallet.core.repositories.sync

import android.support.annotation.VisibleForTesting
import io.realm.kotlin.where
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.models.wallet.LooprWallet
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
@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
open class SyncRepositoryImpl(currentWallet: LooprWallet) : BaseRealmRepository(currentWallet), SyncRepository {

    override fun getLastSyncTime(@SyncData.SyncType syncType: String): Date? {
        return uiSharedRealm.where<SyncData>()
                .equalTo(SyncData::syncType, syncType)
                .findFirst()
                ?.lastSyncTime
    }

    override fun getLastSyncTimeForWallet(address: String, syncType: String): Date? {
        return uiSharedRealm.where<SyncData>()
                .equalTo(SyncData::syncType, syncType)
                .equalTo(SyncData::address, address)
                .findFirst()
                ?.lastSyncTime
    }

}