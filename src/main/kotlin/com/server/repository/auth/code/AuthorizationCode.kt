package com.server.repository.auth.code

import com.server.repository.MongoObject
import com.server.repository.auth.AuthenticationSerializer
import org.springframework.security.oauth2.provider.OAuth2Authentication
import java.time.LocalDateTime

class AuthorizationCode(
        /** The AuthorizationCode itself */
        val code: String,
        /** The serialized Authentication object */
        private val auth: String,
        /** The date the AuthorizationCode expires */
        val expiryDate: LocalDateTime = defaultExpiryDate) : MongoObject() {

    companion object {
        /**
         * @return the default AuthorizationCode expiry date which is 10 minutes in the future of "now"
         */
        val defaultExpiryDate: LocalDateTime
            get() = LocalDateTime.now().plusMinutes(10)
    }

    /**
     * @return the de-serialized Authentication object
     */
    val authentication: OAuth2Authentication?
        get() = AuthenticationSerializer.deserialize(auth)

    /**
     * @return true if the AuthorizationCode has passed it's expiration date
     */
    val isExpired: Boolean
        get() = expiryDate < LocalDateTime.now()
}