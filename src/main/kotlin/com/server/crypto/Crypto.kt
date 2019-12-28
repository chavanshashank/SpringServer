package com.server.crypto

import java.security.Key
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec

abstract class Crypto(
        /** the crypto algorithm to use */
        algorithm: String?,
        /** the encryption key */
        keyString: String?) {

    protected val key: Key
    protected lateinit var cipher: Cipher
        private set

    init {
        if (keyString == null) {
            throw RuntimeException("bad $algorithm key configured")
        }
        key = SecretKeySpec(keyString.toByteArray(), algorithm)
        try {
            cipher = Cipher.getInstance(algorithm)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
    }

    /**
     * Encrypts the provided String using the encryption algorithm provided on creation and returns the encrypted result.
     *
     * @param text The text to encrypt
     * @return The encrypted String
     */
    abstract fun encrypt(text: String?): String?

    /**
     * Decrypts a previously encrypted String and returns the decrypted result.
     *
     * @param text The text to decrypt
     * @return The decrypted String
     */
    abstract fun decrypt(text: String?): String?
}