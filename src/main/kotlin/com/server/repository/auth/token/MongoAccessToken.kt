package com.server.repository.auth.token

import com.server.util.toDate
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import java.time.LocalDateTime

class MongoAccessToken(value: String,
                       auth: String?,
                       username: String?,
                       clientId: String?,
                       expiration: LocalDateTime?,
                       val refreshToken: String?,
                       private val refreshTokenExpiration: LocalDateTime?,
                       val authenticationId: String?,
                       private val type: String?,
                       private val scopes: MutableSet<String>?,
                       private val additionalInfo: MutableMap<String, Any>?) :
        MongoBaseToken(value, auth, username, clientId, expiration) {

    /**
     * @return A Spring OAuth2AccessToken from the MongoAccessToken.
     */
    val oAuth2AccessToken: OAuth2AccessToken
        get() {
            val accessToken = DefaultOAuth2AccessToken(value)
            accessToken.expiration = expiration?.toDate()
            accessToken.tokenType = type
            accessToken.additionalInformation = additionalInfo ?: mapOf() // must not be null
            accessToken.scope = scopes

            if (refreshToken != null) {
                if (refreshTokenExpiration == null) {
                    accessToken.refreshToken = DefaultOAuth2RefreshToken(refreshToken)
                } else {
                    accessToken.refreshToken = DefaultExpiringOAuth2RefreshToken(refreshToken, refreshTokenExpiration.toDate())
                }
            }
            return accessToken
        }
}