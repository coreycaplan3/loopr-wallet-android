package com.caplaninnovations.looprwallet.models.android

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.caplaninnovations.looprwallet.models.android.EncryptionManager.Companion.AES_MODE_V23
import com.caplaninnovations.looprwallet.models.android.EncryptionManager.Companion.ANDROID_KEY_STORE_PROVIDER
import com.caplaninnovations.looprwallet.models.android.EncryptionManager.Companion.KEY_ALIAS
import java.security.*
import javax.crypto.*
import javax.crypto.spec.GCMParameterSpec

/**
 *  Created by Corey on 1/29/2018.
 *  Project: loopr-wallet-android
 * <p></p>
 *  Purpose of Class:
 */
@TargetApi(Build.VERSION_CODES.M)
internal class EncryptionManagerImpl23(context: Activity) : EncryptionManager {

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEY_STORE_PROVIDER)

    init {
        keyStore.load(null)

        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE_PROVIDER);
            val spec = KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(false)
                    .setUserAuthenticationRequired(false)
                    .build()

            keyGenerator.init(spec)
            keyGenerator.generateKey();
        }
    }

    fun encryptData(data: String): String {
        val cipher = Cipher.getInstance(AES_MODE_V23)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), GCMParameterSpec(128, cipher.iv))
        val encodedBytes = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(encodedBytes, Base64.DEFAULT)
    }

    fun decryptData(encryptedData: String): String {
        val cipher = Cipher.getInstance(AES_MODE_V23)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), GCMParameterSpec(128, cipher.iv))
        val decodedBytes = cipher.doFinal(encryptedData.toByteArray())
        return Base64.encodeToString(decodedBytes, Base64.DEFAULT)
    }

    // MARK - PRIVATE FUNCTIONS

    @Throws(KeyStoreException::class, NoSuchAlgorithmException::class, UnrecoverableKeyException::class)
    private fun getSecretKey(): Key {
        return keyStore.getKey(KEY_ALIAS, null)
    }

}