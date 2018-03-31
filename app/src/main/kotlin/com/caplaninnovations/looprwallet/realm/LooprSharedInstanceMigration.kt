package com.caplaninnovations.looprwallet.realm

import io.realm.DynamicRealm
import io.realm.RealmMigration

/**
 * Created by Corey on 1/30/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: Class used to deal with any/all migrations of the app's internal database.
 *
 * A migration occurs when a user downloads an update for the APP and the previous version of the
 * app is running a  lower version of the database from the newer one (in the newly-downloaded
 * version).
 */
class LooprSharedInstanceMigration : RealmMigration {

    override fun migrate(realm: DynamicRealm?, oldVersion: Long, newVersion: Long) {
        for (i in oldVersion..newVersion) {
            when (i) {
                0L -> migrate0To1(realm)
                else -> throw IllegalStateException("Developer did not create a migration plan!")
            }
        }
    }

    // MARK - Private Methods

    private fun migrate0To1(realm: DynamicRealm?) {
    }

}