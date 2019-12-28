package com.server.unit

import com.server.MySpringBootTest
import com.server.config.yml.CryptoConfig
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@MySpringBootTest
class PropertyFileTest {

    @Autowired
    private lateinit var cryptoConfig: CryptoConfig

    @Test
    fun testCryptoConfig() {

        assertNotNull(cryptoConfig.key)
        assertFalse(cryptoConfig.key.isEmpty())

        assertNotNull(cryptoConfig.algorithm)
        assertEquals("AES", cryptoConfig.algorithm)
    }
}