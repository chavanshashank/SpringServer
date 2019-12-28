package com.server.unit

import com.server.repository.auth.AuthenticationSerializer
import com.server.repository.user.User
import com.server.util.TestCreator.createAuthentication
import org.junit.Assert.*
import org.junit.Test

class AuthenticationSerializerTest {

    private val clientId = "client-id"

    @Test
    fun testSerializeDeserialize() {

        val user = User("username", "pw")
        val auth = createAuthentication(user, clientId)

        val serializedAuth = AuthenticationSerializer.serialize(auth)
        assertFalse(serializedAuth.isNullOrEmpty())

        val deserialized = AuthenticationSerializer.deserialize(serializedAuth)
        assertNotNull(deserialized)

        assertEquals(auth, deserialized)
    }
}