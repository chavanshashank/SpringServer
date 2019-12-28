package com.server.repository.auth.token

import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2RefreshToken
import java.util.*

class MongoAccessToken(token: String,
                       private val refreshTokenValue: String?,
                       auth: String?,
                       val authenticationId: String?,
                       private val expirationDate: Date?,
                       private val type: String?,
                       private val scopes: MutableSet<String>,
                       private val additionalInfo: MutableMap<String, Any>?,
                       username: String?,
                       clientId: String?) :
        MongoBaseToken(token, auth, username, clientId), OAuth2AccessToken {

    override fun isExpired(): Boolean {
        return expirationDate != null && expirationDate.before(Date())
    }

    override fun getExpiresIn(): Int {
        return if (expirationDate != null) ((expirationDate.time - System.currentTimeMillis()) / 1000L).toInt() else 0
    }

    override fun getExpiration(): Date? {
        return expirationDate
    }

    override fun getAdditionalInformation(): MutableMap<String, Any>? {
        return additionalInfo
    }

    override fun getTokenType(): String? {
        return type
    }

    override fun getScope(): MutableSet<String> {
        return scopes
    }

    override fun getValue(): String? {
        return token
    }

    override fun getRefreshToken(): OAuth2RefreshToken? {
        return DefaultOAuth2RefreshToken(refreshTokenValue)
    }
}