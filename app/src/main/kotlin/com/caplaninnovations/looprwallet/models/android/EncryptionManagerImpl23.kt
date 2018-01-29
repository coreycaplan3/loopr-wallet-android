package com.caplaninnovations.looprwallet.models.android

import android.annotation.TargetApi
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.caplaninnovations.looprwallet.utilities.SharedPreferenceUtility
import io.realm.RealmConfiguration
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.security.cert.CertificateException

/**
 *  Created by Corey on 1/29/2018.
 *  Project: loopr-wallet-android
 * <p></p>
 *  Purpose of Class:
 */
@TargetApi(Build.VERSION_CODES.M)
internal class EncryptionManagerImpl23(private val context: Activity): EncryptionManager {
    private val rng = SecureRandom()
    private val keyStore = prepareKeyStore()

    val encryptedRealmKey: ByteArray?
        get() = SharedPreferenceUtility.load(context)

    private val keyguardManager: KeyguardManager
        get() = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

    fun containsEncryptionKey(): Boolean {
        try {
            return keyStore.containsAlias(KEY_ALIAS)
        } catch (e: KeyStoreException) {
            throw RuntimeException(e)
        }

    }

    fun unlockKeyStore(requestCode: Int) {
        val intent = keyguardManager.createConfirmDeviceCredentialIntent("Android Keystore System",
                "unlock keystore to decrypt Realm database.")
        context.startActivityForResult(intent, requestCode)
    }

    fun onUnlockKeyStoreResult(result: Int, data: Intent): Boolean {
        return result == Activity.RESULT_OK
    }

    fun generateKeyForRealm(): ByteArray {
        val keyForRealm = ByteArray(RealmConfiguration.KEY_LENGTH)
        rng.nextBytes(keyForRealm)
        return keyForRealm
    }

    fun generateKeyInKeystore() {
        val keyGenerator = try {
            KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    KEYSTORE_PROVIDER_NAME)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: NoSuchProviderException) {
            throw RuntimeException(e)
        }

        val keySpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                .setUserAuthenticationValidityDurationSeconds(
                        AUTH_VALID_DURATION_IN_SECOND)
                .build()
        try {
            keyGenerator.init(keySpec)
        } catch (e: InvalidAlgorithmParameterException) {
            throw RuntimeException(e)
        }

        keyGenerator.generateKey()

    }

    fun encryptAndSaveKeyForRealm(keyForRealm: ByteArray): ByteArray {
        val ks = prepareKeyStore()
        val cipher = prepareCipher()

        val iv: ByteArray
        val encryptedKeyForRealm: ByteArray
        try {
            val key = ks.getKey(KEY_ALIAS, null) as SecretKey
            cipher.init(Cipher.ENCRYPT_MODE, key)

            encryptedKeyForRealm = cipher.doFinal(keyForRealm)
            iv = cipher.getIV()
        } catch (e: InvalidKeyException) {
            throw RuntimeException("key for encryption is invalid", e)
        } catch (e: UnrecoverableKeyException) {
            throw RuntimeException("key for encryption is invalid", e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("key for encryption is invalid", e)
        } catch (e: KeyStoreException) {
            throw RuntimeException("key for encryption is invalid", e)
        } catch (e: BadPaddingException) {
            throw RuntimeException("key for encryption is invalid", e)
        } catch (e: IllegalBlockSizeException) {
            throw RuntimeException("key for encryption is invalid", e)
        }

        val ivAndEncryptedKey = ByteArray(Integer.SIZE + iv.size + encryptedKeyForRealm.size)

        val buffer = ByteBuffer.wrap(ivAndEncryptedKey)
        buffer.order(ORDER_FOR_ENCRYPTED_DATA)
        buffer.putInt(iv.size)
        buffer.put(iv)
        buffer.put(encryptedKeyForRealm)

        SharedPreferenceUtility.save(context, ivAndEncryptedKey)

        return ivAndEncryptedKey
    }

    fun decryptKeyForRealm(ivAndEncryptedKey: ByteArray): ByteArray {
        val cipher = prepareCipher()
        val keyStore = prepareKeyStore()

        val buffer = ByteBuffer.wrap(ivAndEncryptedKey)
        buffer.order(ORDER_FOR_ENCRYPTED_DATA)

        val ivLength = buffer.getInt()
        val iv = ByteArray(ivLength)
        val encryptedKey = ByteArray(ivAndEncryptedKey.size - Integer.SIZE - ivLength)

        buffer.get(iv)
        buffer.get(encryptedKey)

        try {
            val key = keyStore.getKey(KEY_ALIAS, null) as SecretKey
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)

            return cipher.doFinal(encryptedKey)
        } catch (e: InvalidKeyException) {
            throw RuntimeException("key is invalid.")
        } catch (e: UnrecoverableKeyException) {
            throw RuntimeException(e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: BadPaddingException) {
            throw RuntimeException(e)
        } catch (e: KeyStoreException) {
            throw RuntimeException(e)
        } catch (e: IllegalBlockSizeException) {
            throw RuntimeException(e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw RuntimeException(e)
        }

    }

    private fun prepareKeyStore(): KeyStore {
        try {
            val ks = KeyStore.getInstance(KEYSTORE_PROVIDER_NAME)
            ks.load(null)
            return ks
        } catch (e: KeyStoreException) {
            throw RuntimeException(e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: CertificateException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }

    private fun prepareCipher(): Cipher {
        val cipher: Cipher
        try {
            cipher = Cipher.getInstance(TRANSFORMATION)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException(e)
        }

        return cipher
    }

    companion object {
        private val KEYSTORE_PROVIDER_NAME = "AndroidKeyStore"
        private val KEY_ALIAS = "realm_key"
        private val TRANSFORMATION = (KeyProperties.KEY_ALGORITHM_AES
                + "/" + KeyProperties.BLOCK_MODE_CBC
                + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7)
        private val AUTH_VALID_DURATION_IN_SECOND = 30

        private val ORDER_FOR_ENCRYPTED_DATA = ByteOrder.BIG_ENDIAN
    }
}