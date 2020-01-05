package com.server.auth

import com.server.repository.auth.AuthenticationSerializer
import com.server.repository.auth.token.AccessTokenRepository
import com.server.repository.auth.token.MongoAccessToken
import com.server.repository.auth.token.MongoRefreshToken
import com.server.repository.auth.token.RefreshTokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2RefreshToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator
import org.springframework.security.oauth2.provider.token.TokenStore
import java.util.*

/**
 * MongoDB implementation of the TokenStore interface for handling reading and storage of access & refresh tokens.
 */
class MongoTokenStore : TokenStore {

    @Autowired
    private lateinit var refreshTokenDb: RefreshTokenRepository

    @Autowired
    private lateinit var accessTokenDb: AccessTokenRepository

    private val authenticationKeyGenerator = DefaultAuthenticationKeyGenerator()

    override fun readAuthentication(token: OAuth2AccessToken): OAuth2Authentication? {
        return readAuthentication(token.value)
    }

    override fun readAuthentication(token: String): OAuth2Authentication? {
        val accessToken = accessTokenDb.findByToken(token)
        return if (accessToken == null) {
            null
        } else {
            val authentication = accessToken.authentication
            if (authentication == null) {
                accessTokenDb.delete(accessToken)
                null
            } else {
                authentication
            }
        }
    }

    override fun storeAccessToken(token: OAuth2AccessToken, authentication: OAuth2Authentication) {
        val authId = authenticationKeyGenerator.extractKey(authentication)
        val username = if (authentication.isClientOnly) null else authentication.name
        val clientId = if (authentication.oAuth2Request == null) null else authentication.oAuth2Request.clientId

        val refreshToken = token.refreshToken
        val refreshTokenExpiration = if (refreshToken is ExpiringOAuth2RefreshToken) {
            refreshToken.expiration
        } else {
            null
        }
        val oAuth2AccessToken = MongoAccessToken(token.value, AuthenticationSerializer.serialize(authentication), username, clientId, token.expiration, refreshToken.value, refreshTokenExpiration, authId, token.tokenType, token.scope, token.additionalInformation)
        accessTokenDb.save(oAuth2AccessToken)
    }

    override fun readAccessToken(tokenValue: String): OAuth2AccessToken? {
        return accessTokenDb.findByToken(tokenValue)?.oAuth2AccessToken
    }

    override fun removeAccessToken(token: OAuth2AccessToken) {
        accessTokenDb.deleteByToken(token.value)
    }

    override fun removeAccessTokenUsingRefreshToken(refreshToken: OAuth2RefreshToken) {
        accessTokenDb.deleteByRefreshToken(refreshToken.value)
    }

    override fun storeRefreshToken(refreshToken: OAuth2RefreshToken, authentication: OAuth2Authentication) {
        val username = if (authentication.isClientOnly) null else authentication.name
        val clientId = if (authentication.oAuth2Request == null) null else authentication.oAuth2Request.clientId

        val oAuth2RefreshToken = MongoRefreshToken(refreshToken.value, AuthenticationSerializer.serialize(authentication), username, clientId, (refreshToken as? ExpiringOAuth2RefreshToken)?.expiration)
        refreshTokenDb.save(oAuth2RefreshToken)
    }

    override fun readRefreshToken(tokenValue: String): OAuth2RefreshToken? {
        return refreshTokenDb.findByToken(tokenValue)?.oAuth2RefreshToken
    }

    override fun readAuthenticationForRefreshToken(token: OAuth2RefreshToken): OAuth2Authentication? {
        val refreshToken = refreshTokenDb.findByToken(token.value)
        return if (refreshToken == null) {
            null
        } else {
            val authentication = refreshToken.authentication
            if (authentication == null) {
                refreshTokenDb.delete(refreshToken)
                null
            } else {
                authentication
            }
        }
    }

    override fun removeRefreshToken(token: OAuth2RefreshToken) {
        refreshTokenDb.deleteByToken(token.value)
    }

    override fun getAccessToken(authentication: OAuth2Authentication): OAuth2AccessToken? {
        val authenticationId = authenticationKeyGenerator.extractKey(authentication)
        return accessTokenDb.findByAuthenticationId(authenticationId)?.oAuth2AccessToken
    }

    override fun findTokensByClientIdAndUserName(clientId: String, username: String): Collection<OAuth2AccessToken> {
        val tokens = accessTokenDb.findByClientIdAndUsername(clientId, username).map { it.oAuth2AccessToken }
        return ArrayList<OAuth2AccessToken>(tokens)
    }

    override fun findTokensByClientId(clientId: String): Collection<OAuth2AccessToken> {
        val tokens = accessTokenDb.findByClientId(clientId).map { it.oAuth2AccessToken }
        return ArrayList<OAuth2AccessToken>(tokens)
    }
}