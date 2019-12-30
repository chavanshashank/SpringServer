package com.server.unit

import com.server.repository.auth.AuthenticationSerializer
import com.server.repository.auth.code.AuthorizationCode
import com.server.repository.user.User
import com.server.util.TestCreator
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

class AuthorizationCodeTest {

    companion object {
        private const val code = "code"
        private const val auth = "auth"
    }

    @Test
    fun testExpired() {

        val expiryNow = LocalDateTime.now()
        val o1 = AuthorizationCode(code, auth, expiryNow)
        assertTrue(o1.isExpired)

        val defaultExpiry = AuthorizationCode.defaultExpiryDate
        val o2 = AuthorizationCode(code, auth, defaultExpiry)
        assertFalse(o2.isExpired)
        assertTrue(defaultExpiry > LocalDateTime.now())

        val expiryPast = LocalDateTime.now().minusMinutes(1)
        val o3 = AuthorizationCode(code, auth, expiryPast)
        assertTrue(o3.isExpired)
    }

    @Test
    fun testAuthentication() {

        val clientId = "clientId"
        val user = User("user", "pw")
        val auth = AuthenticationSerializer.serialize(TestCreator.createAuthentication(user, clientId))
        assertNotNull(auth)

        if (auth != null) {
            val o = AuthorizationCode(code, auth)
            val authentication = o.authentication
            assertNotNull(authentication)
            assertTrue(authentication?.userAuthentication?.principal is User)

            val authUser = authentication?.userAuthentication?.principal as? User
            assertNotNull(authUser)

            if (authUser != null) {
                assertEquals(user.username, authUser.username)
            }

            assertEquals(clientId, authentication?.oAuth2Request?.clientId)
        }
    }
}