package com.server.crypto

import com.server.config.yml.CryptoConfig
import java.security.InvalidKeyException
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException

class ServerCrypto(cryptoConfig: CryptoConfig) : Crypto(cryptoConfig.algorithm, cryptoConfig.key) {

    /** URL save encoder  */
    private val encoder = Base64.getUrlEncoder()
    /** URL save decoder  */
    private val decoder = Base64.getUrlDecoder()

    @Synchronized
    override fun encrypt(text: String?): String? {
        return if (text == null) {
            null
        } else {
            try {
                cipher.init(Cipher.ENCRYPT_MODE, key)
                return encoder.encodeToString(cipher.doFinal(text.toByteArray()))
            } catch (e: IllegalBlockSizeException) {
                e.printStackTrace()
            } catch (e: BadPaddingException) {
                e.printStackTrace()
            } catch (e: InvalidKeyException) {
                e.printStackTrace()
            }
            null
        }
    }

    @Synchronized
    override fun decrypt(text: String?): String? {
        return if (text == null) {
            null
        } else {
            try {
                cipher.init(Cipher.DECRYPT_MODE, key)
                return String(cipher.doFinal(decoder.decode(text)))
            } catch (e: IllegalBlockSizeException) {
                e.printStackTrace()
            } catch (e: BadPaddingException) {
                e.printStackTrace()
            } catch (e: InvalidKeyException) {
                e.printStackTrace()
            }
            null
        }
    }
}