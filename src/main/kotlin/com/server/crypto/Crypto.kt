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

    protected val aesKey: Key
    protected lateinit var cipher: Cipher

    companion object {
        /**
         * Size of the key in bytes (32 = AES-256)
         */
        private const val KEY_SIZE = 32
    }

    init {
        if (keyString == null || keyString.length != KEY_SIZE) {
            throw RuntimeException("bad $algorithm key configured")
        }
        aesKey = SecretKeySpec(keyString.toByteArray(), algorithm)
        try {
            cipher = Cipher.getInstance(algorithm)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
    }

    /**
     * Encrypts the provided String using AES encryption and returns the encrypted result.
     *
     * @param text the text to encrypt
     * @return the encrypted String
     * @throws Exception
     */
    abstract fun encrypt(text: String?): String?

    /**
     * Decrypts a previously AES encrypted String and returns the decrypted result.
     *
     * @param text the text to decrypt
     * @return the decrypted String
     * @throws Exception
     */
    abstract fun decrypt(text: String?): String?
}