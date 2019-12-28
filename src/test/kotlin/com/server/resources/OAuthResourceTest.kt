package com.server.resources

import com.server.repository.client.Client
import com.server.repository.client.ClientRepository
import com.server.repository.auth.token.AccessTokenRepository
import com.server.repository.auth.token.RefreshTokenRepository
import com.server.repository.user.User
import com.server.repository.user.UserRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.Base64.getUrlEncoder
import javax.servlet.Filter

class OAuthResourceTest : BaseResourceTest() {

    private val encoder = getUrlEncoder()

    private val authHeader: String
        get() = "Basic ${encoder.encodeToString("$clientId:$clientSecret".toByteArray())}"

    @Autowired
    private lateinit var springSecurityFilterChain: Filter

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var clientRepository: ClientRepository

    @Autowired
    private lateinit var accessTokenRepository: AccessTokenRepository

    @Autowired
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    companion object {
        private const val username = "user@user.com"
        private const val password = "123456"
        private val clientId = "5e051ea44f64347c8530c264"
        private const val clientSecret = "client-secret"
    }

    @Before
    override fun setup() {
        mvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .addFilters<DefaultMockMvcBuilder>(springSecurityFilterChain).build()

        userRepository.deleteAll()
        clientRepository.deleteAll()
        accessTokenRepository.deleteAll()
        refreshTokenRepository.deleteAll()

        val user = User(username, passwordEncoder.encode(password))
        assertNotNull(userRepository.save(user))
        assertEquals(1, userRepository.count())

        val client = Client(passwordEncoder.encode(clientSecret),
                scopes = listOf("app"),
                grantTypes = listOf("password", "refresh_token", "client_credentials", "authorization_code"),
                redirectUris = listOf("https://www.example.com"))
        client.id = clientId
        assertNotNull(clientRepository.save(client))
        assertEquals(1, clientRepository.count())
    }

    @Test
    fun testGrantTypePassword() {

        mvc.perform(post("/oauth/token").param("grant_type", "password").param("username", username).param("password", password)
                .header("Authorization", authHeader).contentType(jsonContent)).andExpect(status().isOk)
                .andExpect(jsonPath("$.access_token").exists()).andExpect(jsonPath("$.refresh_token").exists())
        assertEquals(1, accessTokenRepository.count())
        assertEquals(1, refreshTokenRepository.count())
    }

    @Test
    fun testGrantTypeRefreshToken() {

        mvc.perform(post("/oauth/token").param("grant_type", "password").param("username", username).param("password", password)
                .header("Authorization", authHeader).contentType(jsonContent)).andExpect(status().isOk)
                .andExpect(jsonPath("$.access_token").exists()).andExpect(jsonPath("$.refresh_token").exists())
        assertEquals(1, accessTokenRepository.count())
        assertEquals(1, refreshTokenRepository.count())

        val refreshToken = refreshTokenRepository.getTokensByClientIdAndUsername(clientId, username).firstOrNull()?.value
        assertNotNull(refreshToken)
        assertNotNull(accessTokenRepository.removeTokensWithRefreshToken(refreshToken))
        assertEquals(0, accessTokenRepository.count())

        mvc.perform(post("/oauth/token").param("grant_type", "refresh_token").param("refresh_token", refreshToken)
                .header("Authorization", authHeader).contentType(jsonContent)).andExpect(status().isOk)
                .andExpect(jsonPath("$.access_token").exists()).andExpect(jsonPath("$.refresh_token").exists())
        assertEquals(1, accessTokenRepository.count())
        assertEquals(1, refreshTokenRepository.count())
    }

    @Test
    fun testGrantTypeClientCredentials() {

        mvc.perform(post("/oauth/token").param("grant_type", "client_credentials").param("clientId", clientId)
                .header("Authorization", authHeader).contentType(jsonContent)).andExpect(status().isOk)
                .andExpect(jsonPath("$.access_token").exists()).andExpect(jsonPath("$.refresh_token").doesNotExist())
        assertEquals(1, accessTokenRepository.count())
        assertEquals(1, refreshTokenRepository.count())
    }

    @Test
    fun testAuthorizationCode() {


    }
}