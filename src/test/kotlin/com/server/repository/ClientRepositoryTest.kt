package com.server.repository

import com.server.MySpringBootTest
import com.server.repository.client.Client
import com.server.repository.client.ClientRepository
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@MySpringBootTest
class ClientRepositoryTest {

    @Autowired
    private lateinit var clientRepository: ClientRepository

    @Before
    fun setup() {
        clientRepository.deleteAll()
        assertEquals(0, clientRepository.count())
    }

    @After
    fun cleanup() {
        clientRepository.deleteAll()
        assertEquals(0, clientRepository.count())
    }

    @Test
    fun testStoreLoad() {

        val client = Client("secret", scopes = listOf("app"), redirectUris = listOf("https://example.com"))
        assertNotNull(clientRepository.save(client).id)
        assertEquals(1, clientRepository.count())

        val loaded = clientRepository.findByIdOrNull(client.id)
        assertNotNull(loaded)
        assertEquals(client.id, loaded?.id)
        assertEquals(client.registeredRedirectUri.size, loaded?.registeredRedirectUri?.size)
        assertEquals(client.scope.size, loaded?.scope?.size)
        assertTrue(loaded?.isAutoApprove(null) == true)
        assertFalse(loaded?.accessTokenValiditySeconds == 0)
        assertFalse(loaded?.refreshTokenValiditySeconds == 0)
        assertEquals(client.clientSecret, loaded?.clientSecret)
    }
}