package com.server.repository.auth.code

import com.server.repository.MongoObject
import com.server.repository.auth.AuthenticationSerializer
import org.springframework.security.oauth2.provider.OAuth2Authentication
import java.time.LocalDateTime

class AuthorizationCodeObject(val code: String, private val auth: String, val expiryDate: LocalDateTime = defaultExpiryDate) : MongoObject() {

    companion object {
        /**
         * Returns the default authorization code expiry date which is 10 minutes in the future of "now".
         */
        val defaultExpiryDate: LocalDateTime
            get() = LocalDateTime.now().plusMinutes(10)
    }

    val authentication: OAuth2Authentication?
        get() = AuthenticationSerializer.deserialize(auth)

    val isExpired: Boolean
        get() = expiryDate < LocalDateTime.now()
}