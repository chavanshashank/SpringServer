package com.server.repository

import com.server.MySpringBootTest
import com.server.repository.auth.AuthenticationSerializer
import com.server.repository.auth.code.AuthorizationCode
import com.server.repository.auth.code.AuthorizationCodeRepository
import com.server.repository.user.User
import com.server.util.TestCreator
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.junit4.SpringRunner
import java.time.temporal.ChronoUnit

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

        val auth = AuthenticationSerializer.serialize(TestCreator.createAuthentication(User("username", "pw"), "clientId"))
        assertNotNull(auth)

        if (auth != null) {
            val expiryDate = AuthorizationCode.defaultExpiryDate.truncatedTo(ChronoUnit.MILLIS)
            val authCodeObject = AuthorizationCode("code", auth, expiryDate)
            assertNotNull(authorizationCodeRepository.save(authCodeObject).id)
            assertEquals(1, authorizationCodeRepository.count())

            val loaded = authorizationCodeRepository.findByIdOrNull(authCodeObject.id)
            assertNotNull(loaded)
            assertEquals(authCodeObject.id, loaded?.id)
            assertEquals(authCodeObject.code, loaded?.code)
            assertEquals(expiryDate, loaded?.expiryDate)
            assertFalse(loaded?.isExpired == true)
            assertNotNull(loaded?.authentication)

            assertNotNull(authorizationCodeRepository.findByCode(authCodeObject.code))
        }
    }
}