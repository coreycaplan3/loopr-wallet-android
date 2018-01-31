package com.caplaninnovations.looprwallet.realm

import io.realm.DynamicRealm
import io.realm.RealmMigration

/**
 *  Created by Corey on 1/30/2018.
 *  Project: loopr-wallet-android
 * <p></p>
 *  Purpose of Class:
 */
internal class LooprMigration: RealmMigration {

    override fun migrate(realm: DynamicRealm?, oldVersion: Long, newVersion: Long) {
        for(i in oldVersion.rangeTo(newVersion)) {
            when(i) {
                0L -> migrate0To1(realm)
                else -> throw IllegalStateException("Developer did not create a migration plan!")
            }
        }
    }

    // MARK - Private Methods

    private fun migrate0To1(realm: DynamicRealm?) {
    }

}