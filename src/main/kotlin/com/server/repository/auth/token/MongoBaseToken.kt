package com.server.repository.auth.token

import com.server.repository.MongoObject
import com.server.repository.auth.AuthenticationSerializer
import org.springframework.security.oauth2.provider.OAuth2Authentication
import java.time.LocalDateTime

/**
 * Base class of all tokens stored in the database.
 */
abstract class MongoBaseToken(
        /** The token value itself */
        var value: String,
        /** The serialized Authentication object */
        private val auth: String?,
        val username: String?,
        val clientId: String?,
        /** The date the token expires */
        val expiration: LocalDateTime?) : MongoObject() {

    /**
     * @return the de-serialized Authentication object
     */
    val authentication: OAuth2Authentication?
        get() = AuthenticationSerializer.deserialize(auth)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MongoBaseToken

        if (value != other.value) return false
        if (username != other.username) return false
        if (clientId != other.clientId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + (username?.hashCode() ?: 0)
        result = 31 * result + (clientId?.hashCode() ?: 0)
        return result
    }
}