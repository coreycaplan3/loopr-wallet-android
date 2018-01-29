@file:Suppress("unused")

package com.caplaninnovations.looprwallet.models.android

import android.content.Context
import com.caplaninnovations.looprwallet.utilities.isJellybeanR2
import com.caplaninnovations.looprwallet.utilities.loge
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.cert.Certificate

/**
 * Created by Corey Caplan on 1/29/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class: To provide a KeyStore mechanism that will work best for various versions of
 * the OS.
 * <br>
 * This class is meant to be used as an instance and not a singleton.
 *
 *
 * This code was converted to Kotlin as a reference by
 * [theyann on Github](https://gist.github.com/theyann/caf2d0d1003e64984be68ed800aeed8d)
 * @author theyann
 */
class KeyStoreCompat {

    // ----------------------
    // Attributes
    // ----------------------

    private val isUsingAndroidKeyStore: Boolean // This prevents from checking every single time (saves a tiny bunch of cycles)
    private var keyStore: KeyStore? // The actual KeyStore instance that we use internally
    private val context: Context

    /**
     * @return true if the key store is not null (initialized at creation of the instance)
     */
    val isValid: Boolean
        get() = keyStore != null

    // These values can be overridden in one of the constructors, but if you're lazy they're all setup

    private val keyStoreType: String

    /**
     * WARNING this can be decompiled, only use this if you store public keys
     */
    private val password: CharArray
    private val file: String

    // ----------------------
    // Constructors
    // ----------------------

    constructor(context: Context) : this(context, ANDROID_KEYSTORE_TYPE, KEYSTORE_FILE, PASSWORD)

    /**
     * @param context needed for I/O file access
     * @param type type of required key store
     * @param file name of the file that will store the key data
     * @param password password allowing to protect the file
     */
    constructor(context: Context, type: String, file: String, password: CharArray) {
        this.context = context
        this.keyStoreType = type
        this.file = file
        this.password = password

        keyStore = safelyGetKeyStore()

        isUsingAndroidKeyStore = keyStore != null && keyStore!!.type == ANDROID_KEYSTORE_TYPE
    }

    /**
     * @return the type of the key store that has been instantiated
     */
    fun type(): String {
        return if (isValid) keyStore!!.type else "invalid"
    }

    /**
     * Loads the content from the file (see constructors) into the key store instance
     *
     * @param forReading true if the goal is to read from the file, but not write. Note:
     * if API level is 18+, this parameter is not taken into account.
     * @throws IllegalStateException if key store is invalid [KeyStoreCompat.isValid]
     * @throws KeyStoreCompatException if something wrong happened during the execution of the command
     */
    fun load(forReading: Boolean) {
        if (keyStore == null) {
            throw IllegalStateException("KeyStore is null, could not find instance")
        }

        try {
            if (isUsingAndroidKeyStore || !forReading) {
                keyStore!!.load(null)
            } else {
                val fis = context.openFileInput(file)
                keyStore!!.load(fis, password)
                fis.close()
            }
        } catch (e: Exception) {
            throw KeyStoreCompatException("An exception occurred loading the keystore", e)
        }

    }

    /**
     * Stores the whole content of the key store object to the file.
     *
     * Note: for API level 18+ this is not necessary as the AndroidKeyStore automatically stores
     * data as it is added
     * @throws IllegalStateException if key store is invalid [KeyStoreCompat.isValid]
     * @throws KeyStoreCompatException if something wrong happened during the execution of the command
     */
    fun store() {
        if (keyStore == null) {
            throw IllegalStateException("KeyStore is null, could not find instance")
        }

        try {
            if (!isUsingAndroidKeyStore) {
                val fos = context.openFileOutput(file, Context.MODE_PRIVATE)
                keyStore!!.store(fos, password)
                fos.close()
            }
        } catch (e: Exception) {
            throw KeyStoreCompatException("An exception occurred storing the keystore", e)
        }

    }

    /**
     * Safely delete an entry, will be skipped if the alias is not found
     * @param alias name of the entry stored in the store
     * @throws IllegalStateException if key store is invalid [KeyStoreCompat.isValid]
     * @throws KeyStoreCompatException if something wrong happened during the execution of the command
     */
    fun deleteEntryIfExists(alias: String) {
        if (keyStore == null) {
            throw IllegalStateException("KeyStore is null, could not find instance")
        }

        try {
            if (keyStore!!.containsAlias(alias)) {
                keyStore!!.deleteEntry(alias)
            }
        } catch (e: Exception) {
            throw KeyStoreCompatException("An exception occurred deleting an entry in keystore", e)
        }

    }

    /**
     * Set the certificate corresponding to the alias entry in the key store. If an entry already exists for this
     * alias, it will be overridden.
     *
     * @param alias alias for the entry
     * @param certificate certificate to store
     * @throws IllegalStateException if key store is invalid [KeyStoreCompat.isValid]
     * @throws KeyStoreCompatException if something wrong happened during the execution of the command
     */
    fun setCertificateEntry(alias: String, certificate: Certificate) {
        if (keyStore == null) {
            throw IllegalStateException("KeyStore is null, could not find instance")
        }

        try {
            keyStore!!.setCertificateEntry(alias, certificate)
        } catch (e: Exception) {
            throw KeyStoreCompatException("An exception occurred deleting an entry in keystore", e)
        }

    }

    /**
     * Checks whether the key store contains the given alias as an entry
     * @param alias name of the entry to verify
     * @return true if the alias was found
     * @throws IllegalStateException if key store is invalid [KeyStoreCompat.isValid]
     * @throws KeyStoreCompatException if something wrong happened during the execution of the command
     */
    fun containsAlias(alias: String): Boolean {
        if (keyStore == null) {
            throw IllegalStateException("KeyStore is null, could not find instance")
        }

        try {
            return keyStore!!.containsAlias(alias)
        } catch (e: Exception) {
            throw KeyStoreCompatException("An exception occurred deleting an entry in keystore", e)
        }

    }

    /**
     * Retrieves the certificate for the given alias
     * @param alias name of the certificate given when stored
     * @return The certificate for the given alias if found, or null if the alias doesn't exist or if it is not a certificate
     * @throws IllegalStateException if key store is invalid [KeyStoreCompat.isValid]
     * @throws KeyStoreCompatException if something wrong happened during the execution of the command
     */
    fun getCertificate(alias: String): Certificate {
        if (keyStore == null) {
            throw IllegalStateException("KeyStore is null, could not find instance")
        }

        try {
            return keyStore!!.getCertificate(alias)
        } catch (e: Exception) {
            throw IllegalStateException("An exception occurred deleting an entry in keystore", e)
        }

    }

    // ----------------------
    // Private Methods
    // ----------------------

    /**
     * @return get the keystore without worrying (too much) about null safety
     */
    private fun safelyGetKeyStore(): KeyStore? {
        if (keyStore == null) {

            val instanceType = if (isJellybeanR2()) ANDROID_KEYSTORE_TYPE else keyStoreType
            keyStore = internalGetKeyStore(instanceType)

            if (keyStore == null) {
                keyStore = internalGetKeyStore(KeyStore.getDefaultType())
            }
        }

        return keyStore
    }

    /**
     * Internal key store instance getter
     * @param type type of required key store
     * @return key store instance
     */
    private fun internalGetKeyStore(type: String): KeyStore? {
        return try {
            KeyStore.getInstance(type)
        } catch (e: KeyStoreException) {
            loge("Error: ${e.message}", e)
            null
        }
    }

    // ----------------------
    // Inner Classes
    // ----------------------

    /**
     * Exception used in the code so that you know that if it crashes, it crashed with style!
     */
    class KeyStoreCompatException(message: String, cause: Throwable) : RuntimeException(message, cause)

    companion object {

        // -------------
        // Static
        // -------------

        private const val ANDROID_KEYSTORE_TYPE = "AndroidKeyStore"
        private val PASSWORD = "LooprWallet".toCharArray()
        private const val KEYSTORE_FILE = "keystore"
    }
}