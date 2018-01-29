package com.caplaninnovations.looprwallet.models.android

import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.utilities.isJellybeanR2
import com.caplaninnovations.looprwallet.utilities.isMarshmallow

/**
 *  Created by Corey on 1/29/2018.
 *  Project: loopr-wallet-android
 * <p></p>
 *  Purpose of Class: To provide a SDK version independent manager for handling encryption keys
 */
interface EncryptionManager {

    companion object {

        internal const val ANDROID_KEY_STORE_PROVIDER = "AndroidKeyStore";
        internal const val AES_MODE_V23 = "AES/GCM/NoPadding"
        internal const val AES_MODE_V18 = "AES/ECB/PKCS7Padding"
        internal const val KEY_ALIAS = "realm-encryption-key"
        internal const val RSA_MODE = "RSA/ECB/PKCS1Padding"

        fun getInstance(activity: BaseActivity): EncryptionManager {
            return when {
                isMarshmallow() -> EncryptionManagerImpl23(activity)
                isJellybeanR2() -> EncryptionManagerImpl18(activity)
                else -> EncryptionManagerImpl15(activity)
            }
        }
    }

}