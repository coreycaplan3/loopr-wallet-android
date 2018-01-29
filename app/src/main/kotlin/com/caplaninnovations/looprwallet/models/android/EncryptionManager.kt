package com.caplaninnovations.looprwallet.models.android

import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.utilities.isMarshmallow

/**
 *  Created by Corey on 1/29/2018.
 *  Project: loopr-wallet-android
 * <p></p>
 *  Purpose of Class: To provide a SDK version independent manager for handling encryption keys
 */
interface EncryptionManager {

    companion object {
        fun getInstance(activity: BaseActivity): EncryptionManager {
            return if (isMarshmallow()) EncryptionManagerImpl23(activity)
            else EncryptionManagerImpl15(activity)
        }
    }

}