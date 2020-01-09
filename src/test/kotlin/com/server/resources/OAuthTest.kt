package com.server.resources

import com.server.repository.auth.token.AccessTokenRepository
import com.server.repository.auth.token.RefreshTokenRepository
import com.server.repository.client.Client
import com.server.repository.client.ClientRepository
import com.server.repository.user.User
import com.server.repository.user.UserRepository
import org.hamcrest.Matchers.not
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


class OAuthTest : BaseResourceTest() {

    /** Convenience getter that creates a Basic Authorization header (Base64 encoded cliendId & secret) */
    private val basicAuthHeader: RequestPostProcessor
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
        assertEquals(0, accessTokenRepository.count())
        assertEquals(0, refreshTokenRepository.count())

        val user = User(username, passwordEncoder.encode(password))
        assertNotNull(userRepository.save(user))
        assertEquals(1, userRepository.count())

        val client = Client(passwordEncoder.encode(clientSecret),
                scope = listOf("app"),
                grantTypes = listOf("password", "refresh_token", "client_credentials", "authorization_code"),
                redirectUris = listOf(redirectUri, "http://localhost:4200"))
        client.id = clientId
        assertNotNull(clientRepository.save(client))
        assertEquals(1, clientRepository.count())
    }

    @Test
    fun testMeEndpoint() {

        // obtain access & refresh token
        val (accessToken, refreshToken) = obtainTokensWithGrantTypePassword()
        assertFalse(accessToken.isEmpty())
        assertFalse(refreshToken.isEmpty())

        mvc.perform(get("/me")
                .header("Authorization", "Bearer $accessToken"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNotEmpty)
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$._username").doesNotExist())
                .andExpect(jsonPath("$.authorities").doesNotExist()) // not returned by endpoint
                .andExpect(jsonPath("$.credentialsNonExpired").doesNotExist()) // not returned by endpoint
                .andExpect(jsonPath("$.accountNonExpired").doesNotExist()) // not returned by endpoint
                .andExpect(jsonPath("$.accountNonLocked").doesNotExist()) // not returned by endpoint
                .andExpect(jsonPath("$.password").exists())
                .andExpect(jsonPath("$.password").value(not(password))) // returned password is encoded
    }

    @Test
    fun testMeEndpointError() {

        // no authorization header
        mvc.perform(get("/me"))
                .andExpect(status().isUnauthorized)

        // invalid token
        mvc.perform(get("/me")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized)
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
    fun testGrantTypePasswordErrors() {

        // test wrong password
        mvc.perform(post("/oauth/token")
                .param("grant_type", "password")
                .param("client_id", clientId)
                .param("username", username)
                .param("password", "wrong-password")
                .with(basicAuthHeader))
                .andExpect(status().isBadRequest)

        // test wrong username
        mvc.perform(post("/oauth/token")
                .param("grant_type", "password")
                .param("client_id", clientId)
                .param("username", "wrong-username")
                .param("password", password)
                .with(basicAuthHeader))
                .andExpect(status().isBadRequest)

        // test wrong client-id
        mvc.perform(post("/oauth/token")
                .param("grant_type", "password")
                .param("client_id", "wrong-client-id")
                .param("username", username)
                .param("password", password)
                .with(basicAuthHeader))
                .andExpect(status().isUnauthorized)

        // test no auth header
        mvc.perform(post("/oauth/token")
                .param("grant_type", "password")
                .param("client_id", clientId)
                .param("username", username)
                .param("password", password))
                .andExpect(status().isUnauthorized)

        // auth header with client id that does not exist
        mvc.perform(post("/oauth/token")
                .param("grant_type", "password")
                .param("client_id", clientId)
                .param("username", username)
                .param("password", password)
                .with(httpBasic("1e051ea44e64347c8530c261", clientSecret)))
                .andExpect(status().isUnauthorized)

        // auth header with client secret that does not exist (does not match id)
        mvc.perform(post("/oauth/token")
                .param("grant_type", "password")
                .param("client_id", clientId)
                .param("username", username)
                .param("password", password)
                .with(httpBasic(clientId, "wrong-client-secret")))
                .andExpect(status().isUnauthorized)

        assertEquals(0, accessTokenRepository.count())
        assertEquals(0, refreshTokenRepository.count())
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
        assertNotNull(accessTokenRepository.deleteByRefreshToken(refreshToken))
        assertEquals(0, accessTokenRepository.count())

        // obtain a new access token using the refresh token
        mvc.perform(post("/oauth/token").param("grant_type", "refresh_token").param("refresh_token", refreshToken)
                .with(basicAuthHeader).contentType(jsonContent)).andExpect(status().isOk)
                .andExpect(jsonPath("$.access_token").exists()).andExpect(jsonPath("$.refresh_token").exists())
        assertEquals(1, accessTokenRepository.count())
        assertEquals(1, refreshTokenRepository.count())

        // obtaining a new access token does not work without auth header
        mvc.perform(post("/oauth/token").param("grant_type", "refresh_token").param("refresh_token", refreshToken)
                .contentType(jsonContent)).andExpect(status().isUnauthorized)

        // obtaining a new access token does not work with invalid refresh token (aka access token in this case)
        mvc.perform(post("/oauth/token").param("grant_type", "refresh_token").param("refresh_token", accessToken)
                .with(basicAuthHeader).contentType(jsonContent)).andExpect(status().isUnauthorized)
    }

    @Test
    fun testGrantTypeClientCredentials() {

        mvc.perform(post("/oauth/token").param("grant_type", "client_credentials")
                .with(basicAuthHeader).contentType(jsonContent)).andExpect(status().isOk)
                .andExpect(jsonPath("$.access_token").exists()).andExpect(jsonPath("$.refresh_token").doesNotExist())
        assertEquals(1, accessTokenRepository.count())
        // a refresh token is created here because the used client supports the grant type "refresh_token (still does not make much sense for client_credentials grant type as the refresh token is not returned)
        assertEquals(1, refreshTokenRepository.count())

        val invalidAuthHeader = httpBasic("bad-client-id", clientSecret)

        // obtaining access & refresh token with invalid auth header
        mvc.perform(post("/oauth/token").param("grant_type", "client_credentials")
                .with(invalidAuthHeader).contentType(jsonContent)).andExpect(status().isUnauthorized)
    }

    @Test
    fun testGrantTypeAuthorizationCode() {

        mvc.perform(get("/oauth/authorize")
                .param("response_type", "code")
                .param("client_id", clientId)
                .param("redirect_uri", redirectUri))
                .andExpect(status().is3xxRedirection)
                .andExpect(redirectedUrl("http://localhost/login"))
    }

    @Test
    fun testFormLogin() {

        mvc.perform(get("/login")).andExpect(status().isOk)

        mvc.perform(formLogin().user(username).password(password))
                .andExpect(status().is3xxRedirection)
                .andExpect(redirectedUrl("/"))
    }

    @Test
    fun testFormLoginErrors() {

        mvc.perform(formLogin().user(username).password("wrong-password"))
                .andExpect(status().is3xxRedirection)
                .andExpect(redirectedUrl("/login?error"))

        mvc.perform(formLogin().user("wrong-username").password(password))
                .andExpect(status().is3xxRedirection)
                .andExpect(redirectedUrl("/login?error"))
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

    @Test
    fun testInvalidGrantType() {

        mvc.perform(post("/oauth/token").param("grant_type", "invalid_grant_type")
                .with(basicAuthHeader).contentType(jsonContent)).andExpect(status().isBadRequest)
    }

    /**
     * Obtains access & refresh token via grant_type password.
     */
    @Throws(Exception::class)
    private fun obtainTokensWithGrantTypePassword(): Pair<String, String> {

        val result = mvc.perform(post("/oauth/token")
                .param("grant_type", "password")
                .param("client_id", clientId)
                .param("username", username)
                .param("password", password)
                .with(basicAuthHeader)
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