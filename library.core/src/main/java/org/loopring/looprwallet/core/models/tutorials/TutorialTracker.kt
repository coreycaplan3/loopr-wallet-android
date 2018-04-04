package org.loopring.looprwallet.core.models.tutorials

import io.realm.RealmObject

/**
 * Created by Corey on 4/4/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
open class TutorialTracker : RealmObject() {

    var isViewTransfersDismissed: Boolean = false

}