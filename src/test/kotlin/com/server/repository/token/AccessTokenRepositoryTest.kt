package com.server.repository.token

import com.server.repository.auth.token.AccessTokenRepository
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken

class AccessTokenRepositoryTest : BaseTokenRepositoryTest() {

    @Autowired
    private lateinit var accessTokenRepository: AccessTokenRepository

    @Before
    fun setup() {
        accessTokenRepository.deleteAll()
        assertEquals(0, accessTokenRepository.count())
    }

    @After
    fun cleanup() {
        accessTokenRepository.deleteAll()
        assertEquals(0, accessTokenRepository.count())
    }

    @Test
    fun testStoreLoad() {

        val token = createAccessToken()
        assertNotNull(accessTokenRepository.save(token).id)
        assertNotNull(token.id)
        assertEquals(1, accessTokenRepository.count())

        val loaded = accessTokenRepository.findByToken(token.value)
        assertNotNull(loaded)
        assertEquals("at", token.value)
        assertEquals("rt", token.refreshToken)
        assertEquals("authId", token.authenticationId)
        assertEquals(username, token.username)
        assertEquals(clientId, token.clientId)
        assertEquals(token.expiration, loaded?.expiration)
        assertNotNull(token.authentication)
        assertNotNull(token.oAuth2AccessToken)
        assertTrue(token.oAuth2AccessToken.refreshToken is ExpiringOAuth2RefreshToken)
        assertEquals("USER", token.authentication?.authorities?.first()?.authority)

        assertEquals(1, accessTokenRepository.findByClientId(clientId).size)
        assertEquals(0, accessTokenRepository.findByClientId("wrong-client-id").size)
        assertEquals(1, accessTokenRepository.findByClientIdAndUsername(clientId, username).size)
        assertEquals(0, accessTokenRepository.findByClientIdAndUsername("wrong-client-id", username).size)
        assertEquals(0, accessTokenRepository.findByClientIdAndUsername(clientId, "wrong-username").size)
        assertNotNull(accessTokenRepository.findByAuthenticationId(authId))
        assertNull(accessTokenRepository.findByAuthenticationId("wrong-auth-id"))
    }

    @Test
    fun testRemove() {
        val t1 = createAccessToken("at1", "rt1")
        assertNotNull(accessTokenRepository.save(t1).id)
        assertEquals(1, accessTokenRepository.count())

        val t2 = createAccessToken("at2", "rt2")
        assertNotNull(accessTokenRepository.save(t2).id)
        assertEquals(2, accessTokenRepository.count())

        accessTokenRepository.deleteByToken(t1.value)
        assertEquals(1, accessTokenRepository.count())

        accessTokenRepository.deleteByRefreshToken(t2.refreshToken)
        assertEquals(0, accessTokenRepository.count())
    }
}