package com.server.repository.client

import com.server.auth.CustomSimpleGrantedAuthority
import com.server.repository.MongoObject
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.provider.ClientDetails

class Client(private val secret: String,
             /** The time in seconds the access tokens of the client are valid, default = 3600 (1 hour) */
             private val accessTokenValidity: Int = 3600,
             /** The time in seconds the refresh tokens of the client are valid, default = 0 (never expires) */
             private val refreshTokenValidity: Int = 0,
             private val secretRequired: Boolean = true,
             private val autoApprove: Boolean = true,
             private val resources: List<String> = listOf(),
             private val redirectUris: List<String> = listOf(),
             private val scopes: List<String>,
             private val grantTypes: List<String> = listOf(),
             private val grantedAuthorities: List<String> = listOf()) : MongoObject(), ClientDetails {

    companion object {
        private const val serialVersionUID = -2918540686806255887L
    }

    override fun isSecretRequired(): Boolean {
        return secretRequired
    }

    override fun getAdditionalInformation(): MutableMap<String, Any> {
        return mutableMapOf()
    }

    override fun getAccessTokenValiditySeconds(): Int {
        return accessTokenValidity
    }

    override fun getResourceIds(): MutableSet<String> {
        return resources.toMutableSet()
    }

    override fun getClientId(): String {
        return id
    }

    override fun isAutoApprove(scope: String?): Boolean {
        return autoApprove
    }

    override fun getAuthorities(): List<GrantedAuthority> {
        return grantedAuthorities.map { CustomSimpleGrantedAuthority(it) }
    }

    override fun getRefreshTokenValiditySeconds(): Int {
        return refreshTokenValidity
    }

    override fun getClientSecret(): String {
        return secret
    }

    override fun getRegisteredRedirectUri(): MutableSet<String> {
        return redirectUris.toMutableSet()
    }

    override fun isScoped(): Boolean {
        return true
    }

    override fun getScope(): MutableSet<String> {
        return scopes.toMutableSet()
    }

    override fun getAuthorizedGrantTypes(): MutableSet<String> {
        return grantTypes.toMutableSet()
    }
}