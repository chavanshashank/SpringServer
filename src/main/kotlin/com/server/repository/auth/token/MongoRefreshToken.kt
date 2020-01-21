package com.server.repository.auth.token

import com.server.util.toDate
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken
import org.springframework.security.oauth2.common.OAuth2RefreshToken
import java.time.LocalDateTime

class MongoRefreshToken(value: String, auth: String?, username: String?, clientId: String?, expiration: LocalDateTime?) :
        MongoBaseToken(value, auth, username, clientId, expiration) {

    /**
     * @return A Spring OAuth2RefreshToken from the MongoRefreshToken.
     */
    val oAuth2RefreshToken: OAuth2RefreshToken
        get() {
            return if (expiration == null) {
                DefaultOAuth2RefreshToken(value)
            } else {
                DefaultExpiringOAuth2RefreshToken(value, expiration.toDate())
            }
        }
}