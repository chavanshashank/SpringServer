package com.server.auth

import com.server.repository.user.UserRepository
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.TokenRequest
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import javax.naming.AuthenticationException

class CustomTokenServices(private val userRepository: UserRepository) : DefaultTokenServices() {

    override fun loadAuthentication(accessTokenValue: String?): OAuth2Authentication {
        val oauthAuthentication = super.loadAuthentication(accessTokenValue)
        val username = oauthAuthentication.userAuthentication?.name

        // username may be null if e.g. client_credentials grant type was used to obtain a token
        return if (username.isNullOrEmpty()) {
            oauthAuthentication
        } else {
            if (userRepository.findByUsername(username) != null) {
                oauthAuthentication
            } else {
                // if no user related to the token exists - revoke it
                revokeToken(accessTokenValue)
                throw InvalidTokenException("Invalid user $username associated with token (user not found)")
            }
        }
    }

    @Throws(AuthenticationException::class)
    override fun refreshAccessToken(refreshTokenValue: String?, tokenRequest: TokenRequest): OAuth2AccessToken {

        try {
            return super.refreshAccessToken(refreshTokenValue, tokenRequest)
        } catch (e: InvalidGrantException) {
            throw InvalidTokenException("Invalid refresh token (not found): $refreshTokenValue")
        } catch (e: NullPointerException) {
            throw InvalidTokenException("Something went wrong (NullPointerException), probably no authentication for token: $refreshTokenValue found")
        }
    }
}