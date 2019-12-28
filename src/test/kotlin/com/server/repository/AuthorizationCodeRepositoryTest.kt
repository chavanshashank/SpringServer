package com.server.repository

import com.server.MySpringBootTest
import com.server.auth.CustomSimpleGrantedAuthority
import com.server.repository.auth.AuthenticationSerializer
import com.server.repository.auth.code.AuthorizationCodeObject
import com.server.repository.auth.code.AuthorizationCodeRepository
import com.server.repository.user.User
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.OAuth2Request
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@MySpringBootTest
class AuthorizationCodeRepositoryTest {

    @Autowired
    private lateinit var authorizationCodeRepository: AuthorizationCodeRepository

    @Before
    fun setup() {
        authorizationCodeRepository.deleteAll()
        assertEquals(0, authorizationCodeRepository.count())
    }

    @After
    fun cleanup() {
        authorizationCodeRepository.deleteAll()
        assertEquals(0, authorizationCodeRepository.count())
    }

    @Test
    fun testStoreLoad() {

        val auth = AuthenticationSerializer.serialize(createAuthentication())
        assertNotNull(auth)

        if (auth != null) {
            val authCodeObject = AuthorizationCodeObject("code", auth)
            assertNotNull(authorizationCodeRepository.save(authCodeObject).id)
            assertEquals(1, authorizationCodeRepository.count())

            val loaded = authorizationCodeRepository.findByIdOrNull(authCodeObject.id)
            assertNotNull(loaded)
            assertEquals(authCodeObject.id, loaded?.id)
            assertEquals(authCodeObject.code, loaded?.code)
            assertNotNull(loaded?.authentication)

            assertNotNull(authorizationCodeRepository.findByCode(authCodeObject.code))
        }
    }

    private fun createAuthentication(): OAuth2Authentication {
        val authorities = listOf(CustomSimpleGrantedAuthority("USER"))
        val oAuth2Request = OAuth2Request(emptyMap(), "clientId", authorities, true, emptySet(), emptySet(), null, emptySet(), emptyMap())
        val authenticationToken = UsernamePasswordAuthenticationToken(User("username", "pw"), null, authorities)
        return OAuth2Authentication(oAuth2Request, authenticationToken)
    }
}