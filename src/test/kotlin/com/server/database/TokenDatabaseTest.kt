package com.server.database

import com.server.MySpringBootTest
import com.server.auth.CustomSimpleGrantedAuthority
import com.server.database.token.*
import com.server.database.user.User
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.OAuth2Request
import org.springframework.test.context.junit4.SpringRunner
import java.util.*


@RunWith(SpringRunner::class)
@MySpringBootTest
class TokenDatabaseTest {

    companion object {
        private const val username = "username"
        private const val clientId = "clientId"
        private const val authId = "authId"
    }

    @Autowired
    private lateinit var accessTokenRepository: AccessTokenRepository

    @Autowired
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @Before
    fun setup() {
        accessTokenRepository.deleteAll()
        refreshTokenRepository.deleteAll()
        assertEquals(0, accessTokenRepository.count())
        assertEquals(0, refreshTokenRepository.count())
    }

    @After
    fun cleanup() {
        accessTokenRepository.deleteAll()
        refreshTokenRepository.deleteAll()
        assertEquals(0, accessTokenRepository.count())
        assertEquals(0, refreshTokenRepository.count())
    }

    @Test
    fun testStoreLoadAccessToken() {

        val token = createAccessToken()
        assertNotNull(accessTokenRepository.save(token).id)
        assertNotNull(token.id)
        assertEquals(1, accessTokenRepository.count())

        val loaded = accessTokenRepository.getTokenByValue(token.value)
        assertNotNull(loaded)
        assertEquals("at", token.value)
        assertEquals("rt", token.refreshToken?.value)
        assertEquals("authId", token.authenticationId)
        assertEquals(username, token.username)
        assertEquals(clientId, token.clientId)
        assertEquals(token.expiration, loaded?.expiration)
        assertNotNull(token.authentication)
        assertEquals("USER", token.authentication?.authorities?.first()?.authority)

        assertEquals(1, accessTokenRepository.getTokensByClientId(clientId).size)
        assertEquals(0, accessTokenRepository.getTokensByClientId("wrong-client-id").size)
        assertEquals(1, accessTokenRepository.getTokensByClientIdAndUsername(clientId, username).size)
        assertEquals(0, accessTokenRepository.getTokensByClientIdAndUsername("wrong-client-id", username).size)
        assertEquals(0, accessTokenRepository.getTokensByClientIdAndUsername(clientId, "wrong-username").size)
        assertNotNull(accessTokenRepository.getTokenByAuthenticationId(authId))
        assertNull(accessTokenRepository.getTokenByAuthenticationId("wrong-auth-id"))
    }

    @Test
    fun testStoreLoadRefreshToken() {

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
    fun testRemoveAccessToken() {
        val t1 = createAccessToken("at1", "rt1")
        assertNotNull(accessTokenRepository.save(t1).id)
        assertEquals(1, accessTokenRepository.count())

        val t2 = createAccessToken("at2", "rt2")
        assertNotNull(accessTokenRepository.save(t2).id)
        assertEquals(2, accessTokenRepository.count())

        accessTokenRepository.removeTokensByValue(t1.value)
        assertEquals(1, accessTokenRepository.count())

        accessTokenRepository.removeTokensWithRefreshToken(t2.refreshToken?.value)
        assertEquals(0, accessTokenRepository.count())
    }

    @Test
    fun testRemoveRefreshToken() {
        val token = createRefreshToken()
        assertNotNull(refreshTokenRepository.save(token).id)
        assertEquals(1, refreshTokenRepository.count())

        refreshTokenRepository.removeTokensByValue(token.value)
        assertEquals(0, refreshTokenRepository.count())
    }

    private fun createAccessToken(token: String = "at", refreshToken: String = "rt"): MongoAccessToken {
        val user = User(username, "pw")
        val date = Date()
        val authObject = createAuthentication(user)
        return MongoAccessToken(token, refreshToken, AuthenticationSerializer.serialize(authObject), authId, date, "bearer", mutableSetOf("app"), null, username, clientId)
    }

    private fun createRefreshToken(token: String = "rt"): MongoRefreshToken {
        val user = User(username, "pw")
        val authObject = createAuthentication(user)
        return MongoRefreshToken(token, AuthenticationSerializer.serialize(authObject), username, clientId)
    }

    private fun createAuthentication(user: User): OAuth2Authentication {
        val authorities = listOf(CustomSimpleGrantedAuthority("USER"))
        val oAuth2Request = OAuth2Request(emptyMap(), clientId, authorities, true, emptySet(), emptySet(), null, emptySet(), emptyMap())
        val authenticationToken = UsernamePasswordAuthenticationToken(user, null, authorities)
        return OAuth2Authentication(oAuth2Request, authenticationToken)
    }
}