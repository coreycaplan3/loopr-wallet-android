package com.caplaninnovations.looprwallet.repositories.sync

import android.support.annotation.VisibleForTesting
import com.caplaninnovations.looprwallet.extensions.equalTo
import com.caplaninnovations.looprwallet.models.user.SyncData
import com.caplaninnovations.looprwallet.models.wallet.LooprWallet
import com.caplaninnovations.looprwallet.repositories.BaseRealmRepository
import io.realm.kotlin.where
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