@file:Suppress("DEPRECATION")

package com.caplaninnovations.looprwallet.models.android

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyProperties
import com.caplaninnovations.looprwallet.models.android.EncryptionManager.Companion.ANDROID_KEY_STORE_PROVIDER
import com.caplaninnovations.looprwallet.models.android.EncryptionManager.Companion.KEY_ALIAS
import com.caplaninnovations.looprwallet.models.android.EncryptionManager.Companion.RSA_MODE
import com.caplaninnovations.looprwallet.utilities.SharedPreferenceUtility
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import javax.security.auth.x500.X500Principal
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.SecureRandom
import java.util.Calendar
import android.util.Base64
import com.caplaninnovations.looprwallet.models.android.EncryptionManager.Companion.AES_MODE_V18
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.spec.SecretKeySpec


/**
 * Created by Corey Caplan on 1/29/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
internal class EncryptionManagerImpl18(private val context: Activity) : EncryptionManager {

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEY_STORE_PROVIDER)

    init {
        keyStore.load(null)

        if (!keyStore.containsAlias(KEY_ALIAS)) {
            // Generate a key pair for encryption
            val start = Calendar.getInstance()
            val end = Calendar.getInstance()
            end.add(Calendar.YEAR, 100) // Certificate expires 100 years from now
            val spec = KeyPairGeneratorSpec.Builder(context)
                    .setAlias(KEY_ALIAS)
                    .setSubject(X500Principal("CN=" + KEY_ALIAS))
                    .setSerialNumber(BigInteger.TEN)
                    .setStartDate(start.time)
                    .setEndDate(end.time)
                    .build()

            val kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE_PROVIDER)
            kpg.initialize(spec)
            kpg.generateKeyPair()
        }

        var encryptedKeyB64 = SharedPreferenceUtility.get(context, KEY_ALIAS)
        if (encryptedKeyB64 == null) {
            val key = ByteArray(16)
            val secureRandom = SecureRandom()
            secureRandom.nextBytes(key)

            encryptedKeyB64 = Base64.encodeToString(rsaEncrypt(key), Base64.DEFAULT)
            SharedPreferenceUtility.put(context, KEY_ALIAS, encryptedKeyB64)
        }
    }

    fun encrypt(decryptedBytes: ByteArray): String {
        val cipher = Cipher.getInstance(AES_MODE_V18, "BC")
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val encodedBytes = cipher.doFinal(decryptedBytes)
        return Base64.encodeToString(encodedBytes, Base64.DEFAULT)
    }


    fun decrypt(encryptedBytes: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(AES_MODE_V18, "BC")
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey())
        return cipher.doFinal(encryptedBytes)
    }

    // MARK - Private Methods

    @Throws(Exception::class)
    private fun getSecretKey(): Key {
        val encryptedKeyB64 = SharedPreferenceUtility.get(context, KEY_ALIAS)
        val encryptedKey = Base64.decode(encryptedKeyB64, Base64.DEFAULT)
        val key = rsaDecrypt(encryptedKey)
        return SecretKeySpec(key, "AES")
    }

    @Throws(Exception::class)
    private fun rsaEncrypt(secret: ByteArray): ByteArray {
        val privateKeyEntry = keyStore.getEntry(EncryptionManager.KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
        // Encrypt the text
        val inputCipher = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL")
        inputCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.certificate.publicKey)

        val outputStream = ByteArrayOutputStream()
        val cipherOutputStream = CipherOutputStream(outputStream, inputCipher)
        cipherOutputStream.write(secret)
        cipherOutputStream.close()

        return outputStream.toByteArray()
    }

    @Throws(Exception::class)
    private fun rsaDecrypt(encrypted: ByteArray): ByteArray {
        val privateKeyEntry = keyStore.getEntry(EncryptionManager.KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
        val output = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL")
        output.init(Cipher.DECRYPT_MODE, privateKeyEntry.privateKey)

        val cipherInputStream = CipherInputStream(ByteArrayInputStream(encrypted), output)
        val values = ArrayList<Byte>()

        while (cipherInputStream.available() > 0) {
            values.add(cipherInputStream.read().toByte())
        }

        val bytes = ByteArray(values.size)
        for (i in bytes.indices) {
            bytes[i] = values[i]
        }
        return bytes
    }

}

