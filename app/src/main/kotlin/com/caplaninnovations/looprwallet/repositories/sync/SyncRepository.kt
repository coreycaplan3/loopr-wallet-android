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
interface SyncRepository: BaseRepository<RealmModel> {

    companion object {
        fun getInstance(currentWallet: LooprWallet) = SyncRepositoryImpl(currentWallet)
    }

    fun getLastSyncTime(@SyncData.SyncType syncType: String): Date?

}