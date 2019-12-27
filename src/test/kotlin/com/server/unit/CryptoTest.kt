package com.server.unit

import com.server.crypto.ServerCrypto
import org.junit.Assert.*
import org.junit.Test
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator

class CryptoTest {

    companion object {
        private const val key = "dkIe45uZerhJkdp9dkIe45uZXrhFkdD0"
        private val crypto = ServerCrypto(key)
    }

    @Test
    fun testNormalEncryptDecrypt() {
        val secretMessage = "Secret Message String"
        val encryptedMessage = crypto.encrypt(secretMessage)
        assertNotEquals(secretMessage, encryptedMessage)
        assertFalse(secretMessage.contains("/"))
        val decryptedMessage = crypto.decrypt(encryptedMessage)
        assertEquals(secretMessage, decryptedMessage)
    }

    @Test
    fun tesURLSave() {
        val secretMessage = "58c0378046e0fb00050b9cac"
        val encryptedMessage = crypto.encrypt(secretMessage)
        assertNotEquals(secretMessage, encryptedMessage)
        assertFalse(secretMessage.contains("/"))
        val decryptedMessage = crypto.decrypt(encryptedMessage)
        assertEquals(secretMessage, decryptedMessage)
    }

    @Test
    fun testEmptyMessage() {
        val secretMessage = ""
        val encryptedMessage = crypto.encrypt(secretMessage)
        assertNotEquals(secretMessage, encryptedMessage)
        assertFalse(secretMessage.contains("/"))
        val decryptedMessage = crypto.decrypt(encryptedMessage)
        assertEquals(secretMessage, decryptedMessage)
    }

    @Test
    fun testNullMessage() {
        val secretMessage: String? = null
        val encryptedMessage = crypto.encrypt(secretMessage)
        assertNull(encryptedMessage)
        val decryptedMessage = crypto.decrypt(encryptedMessage)
        assertEquals(secretMessage, decryptedMessage)
    }

    @Test
    fun testLoop() {
        val size = 10
        val generator = RandomValueStringGenerator(48)
        val messages: MutableList<String> = ArrayList()
        for (i in 0 until size) {
            val secretMessage = generator.generate()
            assertFalse(messages.contains(secretMessage))
            assertTrue(messages.add(secretMessage))
            val encryptedMessage = crypto.encrypt(secretMessage)
            assertNotEquals(secretMessage, encryptedMessage)
            assertFalse(secretMessage.contains("/"))
            val decryptedMessage = crypto.decrypt(encryptedMessage)
            assertEquals(secretMessage, decryptedMessage)
        }
        assertEquals(messages.size.toLong(), size.toLong())
    }
}