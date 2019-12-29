package com.server.resources

import com.server.repository.auth.token.AccessTokenRepository
import com.server.repository.auth.token.RefreshTokenRepository
import com.server.repository.client.Client
import com.server.repository.client.ClientRepository
import com.server.repository.user.User
import com.server.repository.user.UserRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.json.JacksonJsonParser
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap


class OAuthTest : BaseResourceTest() {

    /** Convenience getter that creates a Basic Authentication header (Base64 encoded cliendId & secret) */
    private val authHeader: RequestPostProcessor
        get() = httpBasic(clientId, clientSecret)

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
        private const val clientId = "5e051ea44f64347c8530c264"
        private const val clientSecret = "client-secret"
        private const val redirectUri = "https://www.example.com"
    }

    @Before
    override fun setup() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply<DefaultMockMvcBuilder>(springSecurity()).build()

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
                redirectUris = listOf(redirectUri))
        client.id = clientId
        assertNotNull(clientRepository.save(client))
        assertEquals(1, clientRepository.count())
    }

    @Test
    fun testGrantTypePassword() {

        val (accessToken, refreshToken) = obtainTokensWithGrantTypePassword()
        assertFalse(accessToken.isEmpty())
        assertFalse(refreshToken.isEmpty())
        assertEquals(1, accessTokenRepository.count())
        assertEquals(1, refreshTokenRepository.count())
    }

    @Test
    fun testGrantTypeRefreshToken() {

        // obtain access & refresh token
        val (accessToken, refreshToken) = obtainTokensWithGrantTypePassword()
        assertFalse(accessToken.isEmpty())
        assertFalse(refreshToken.isEmpty())
        assertEquals(1, accessTokenRepository.count())
        assertEquals(1, refreshTokenRepository.count())

        // remove the previous access token
        assertNotNull(accessTokenRepository.removeTokensWithRefreshToken(refreshToken))
        assertEquals(0, accessTokenRepository.count())

        // obtain a new access token using the refresh token
        mvc.perform(post("/oauth/token").param("grant_type", "refresh_token").param("refresh_token", refreshToken)
                .with(authHeader).contentType(jsonContent)).andExpect(status().isOk)
                .andExpect(jsonPath("$.access_token").exists()).andExpect(jsonPath("$.refresh_token").exists())
        assertEquals(1, accessTokenRepository.count())
        assertEquals(1, refreshTokenRepository.count())
    }

    @Test
    fun testGrantTypeClientCredentials() {

        mvc.perform(post("/oauth/token").param("grant_type", "client_credentials").param("clientId", clientId)
                .with(authHeader).contentType(jsonContent)).andExpect(status().isOk)
                .andExpect(jsonPath("$.access_token").exists()).andExpect(jsonPath("$.refresh_token").doesNotExist())
        assertEquals(1, accessTokenRepository.count())
        assertEquals(1, refreshTokenRepository.count())
    }

    @Test
    fun testGrantTypeAuthorizationCode() {

        mvc.perform(get("/oauth/authorize")
                .param("response_type", "code")
                .param("clientId", clientId)
                .param("redirect_uri", redirectUri))
                .andExpect(status().is3xxRedirection)
                .andExpect(redirectedUrl("http://localhost/login"))
    }

    @Test
    fun testFormLogin() {

        mvc.perform(get("/login")).andExpect(status().isOk)

        mvc.perform(formLogin().user(username).password(password))
                .andExpect(status().isFound)
                .andExpect(redirectedUrl("/"))
    }

    @Test
    fun testResourceSecurity() {

        val (accessToken, refreshToken) = obtainTokensWithGrantTypePassword()
        assertFalse(accessToken.isEmpty())
        assertFalse(refreshToken.isEmpty())

        mvc.perform(get("/api/secured")).andExpect(status().isUnauthorized)
        mvc.perform(get("/api/secured").header("Authorization", "Bearer $accessToken")).andExpect(status().isOk)
        mvc.perform(get("/api/open")).andExpect(status().isOk)
        mvc.perform(get("/api/open").header("Authorization", "Bearer $accessToken")).andExpect(status().isOk)
    }

    /**
     * Obtains access & refresh token via grant_type password.
     */
    @Throws(Exception::class)
    private fun obtainTokensWithGrantTypePassword(): Pair<String, String> {
        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add("grant_type", "password")
        params.add("client_id", clientId)
        params.add("username", username)
        params.add("password", password)
        val result = mvc.perform(post("/oauth/token")
                .params(params)
                .with(httpBasic(clientId, clientSecret))
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().isOk)
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.scope").exists())
                .andExpect(jsonPath("$.expires_in").exists())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists()).andReturn()
        val resultString = result.response.contentAsString
        val jsonParser = JacksonJsonParser()
        return Pair(jsonParser.parseMap(resultString)["access_token"].toString(), jsonParser.parseMap(resultString)["refresh_token"].toString())
    }
}