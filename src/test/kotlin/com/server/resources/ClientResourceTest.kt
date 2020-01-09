package com.server.resources

import com.server.repository.client.Client
import com.server.repository.client.ClientRepository
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ClientResourceTest : BaseResourceTest() {

    @Autowired
    private lateinit var clientRepository: ClientRepository

    @Before
    override fun setup() {
        super.setup()
        clientRepository.deleteAll()
        assertEquals(0, clientRepository.count())
    }

    @After
    fun cleanup() {
        clientRepository.deleteAll()
        assertEquals(0, clientRepository.count())
    }

    @Test
    fun testCreateClient() {

        val client = Client("secret", scope = listOf("app"), redirectUris = listOf("https://example.com", "http://localhost:4200"))
        assertEquals(0, clientRepository.count())

        val json = toJson(client)
        mvc.perform(post("/api/client").content(json).contentType(jsonContent)).andExpect(status().isOk)
        assertEquals(1, clientRepository.count())

        val loaded = clientRepository.findAll().firstOrNull()
        assertNotNull(loaded)
        assertNotNull(loaded?.id) // database assigned id
        assertFalse(loaded?.accessTokenValiditySeconds == 0)
        assertNotEquals(client.secret, loaded?.secret) // secret is encoded
    }
}