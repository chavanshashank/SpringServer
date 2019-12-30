package com.server.repository.auth.code

import com.server.repository.MongoObject
import com.server.repository.auth.AuthenticationSerializer
import org.springframework.security.oauth2.provider.OAuth2Authentication
import java.time.LocalDateTime

class AuthorizationCodeObject(val code: String, private val auth: String, val expiryDate: LocalDateTime) : MongoObject() {

    companion object {
        const val defaultExpiryMinutes = 10L
    }

    val authentication: OAuth2Authentication?
        get() = AuthenticationSerializer.deserialize(auth)

    val isExpired: Boolean
        get() = expiryDate < LocalDateTime.now()
}