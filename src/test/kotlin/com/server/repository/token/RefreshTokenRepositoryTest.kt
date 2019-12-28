package com.server.repository.token

import com.server.repository.auth.token.RefreshTokenRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class RefreshTokenRepositoryTest : BaseTokenRepositoryTest() {

    @Autowired
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @Before
    fun setup() {
        refreshTokenRepository.deleteAll()
        assertEquals(0, refreshTokenRepository.count())
    }

    @After
    fun cleanup() {
        refreshTokenRepository.deleteAll()
        assertEquals(0, refreshTokenRepository.count())
    }

    @Test
    fun testStoreLoad() {

        val token = createRefreshToken()
        assertNotNull(refreshTokenRepository.save(token).id)
        assertNotNull(token.id)
        assertEquals(1, refreshTokenRepository.count())

        val loaded = refreshTokenRepository.getTokenByValue(token.value)
        assertNotNull(loaded)
        assertEquals("rt", token.value)
        assertEquals(username, token.username)
        assertEquals(clientId, token.clientId)
        assertNotNull(token.authentication)
        assertEquals("USER", token.authentication?.authorities?.first()?.authority)
    }

    @Test
    fun testRemove() {
        val token = createRefreshToken()
        assertNotNull(refreshTokenRepository.save(token).id)
        assertEquals(1, refreshTokenRepository.count())

        refreshTokenRepository.removeTokensByValue(token.value)
        assertEquals(0, refreshTokenRepository.count())
    }
}