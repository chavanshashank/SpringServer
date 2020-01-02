package com.server.repository.token

import com.server.auth.MongoTokenStore
import com.server.repository.auth.token.AccessTokenRepository
import com.server.repository.auth.token.RefreshTokenRepository
import com.server.repository.user.User
import com.server.util.TestCreator
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken

class MongoTokenStoreTest : BaseTokenRepositoryTest() {

    @Autowired
    private lateinit var tokenStore: MongoTokenStore

    @Autowired
    private lateinit var accessTokenRepository: AccessTokenRepository

    @Autowired
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    companion object {
        private val user = User("username", "pw")
    }

    @Before
    fun setup() {
        accessTokenRepository.deleteAll()
        assertEquals(0, accessTokenRepository.count())
        refreshTokenRepository.deleteAll()
        assertEquals(0, refreshTokenRepository.count())
    }

    @After
    fun cleanup() {
        accessTokenRepository.deleteAll()
        assertEquals(0, accessTokenRepository.count())
        refreshTokenRepository.deleteAll()
        assertEquals(0, refreshTokenRepository.count())
    }

    @Test
    fun testStoreLoadRemoveAccessToken() {

        val token = DefaultOAuth2AccessToken("at-value")
        val refreshToken = DefaultOAuth2RefreshToken("rt-value")
        token.refreshToken = refreshToken
        val auth = TestCreator.createAuthentication(user, clientId)
        tokenStore.storeAccessToken(token, auth)
        assertEquals(1, accessTokenRepository.count())

        val loaded = tokenStore.readAccessToken(token.value)
        assertNotNull(loaded)
        assertEquals(token.value, loaded?.value)

        assertNotNull(tokenStore.getAccessToken(auth))

        assertEquals(1, tokenStore.findTokensByClientId(clientId).size)
        assertEquals(1, tokenStore.findTokensByClientIdAndUserName(clientId, username).size)

        val loadedAuth = tokenStore.readAuthentication(token)
        assertNotNull(loadedAuth)
        assertEquals(auth, loadedAuth)

        tokenStore.removeAccessToken(token)
        assertEquals(0, accessTokenRepository.count())

        tokenStore.storeAccessToken(token, auth)
        assertEquals(1, accessTokenRepository.count())

        tokenStore.removeAccessTokenUsingRefreshToken(refreshToken)
        assertNull(tokenStore.readAccessToken(token.value))
        assertNull(tokenStore.readAuthentication(token))
        assertEquals(0, accessTokenRepository.count())
    }

    @Test
    fun testStoreLoadRemoveRefreshToken() {

        val token = DefaultOAuth2RefreshToken("rt-value")
        val auth = TestCreator.createAuthentication(user, clientId)
        tokenStore.storeRefreshToken(token, auth)
        assertEquals(1, refreshTokenRepository.count())

        val loaded = tokenStore.readRefreshToken(token.value)
        assertNotNull(loaded)
        assertEquals(token.value, loaded?.value)

        val loadedAuth = tokenStore.readAuthenticationForRefreshToken(token)
        assertNotNull(loadedAuth)
        assertEquals(auth, loadedAuth)

        tokenStore.removeRefreshToken(token)
        assertNull(tokenStore.readRefreshToken(token.value))
        assertNull(tokenStore.readAuthenticationForRefreshToken(token))
        assertEquals(0, refreshTokenRepository.count())
    }
}