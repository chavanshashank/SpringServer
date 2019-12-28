package com.server.unit

import com.server.MySpringBootTest
import com.server.crypto.ServerCrypto
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@MySpringBootTest
class CryptoTest {

    @Autowired
    private lateinit var crypto: ServerCrypto

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