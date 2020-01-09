package com.server.repository.client

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.server.auth.CustomSimpleGrantedAuthority
import com.server.repository.MongoObject
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.provider.ClientDetails

class Client(var secret: String?,
             /** The time in seconds the access tokens of the client are valid, default = 3600 (1 hour) */
             private val accessTokenValidity: Int = 3600,
             /** The time in seconds the refresh tokens of the client are valid, default = 315569520 / 10 years (0 = never expires) */
             private val refreshTokenValidity: Int = 315569520,
             private val secretRequired: Boolean = true,
             private val autoApprove: Boolean = true,
             private val resources: List<String> = listOf(),
             private val redirectUris: List<String> = listOf(),
             private val scope: List<String>,
             private val grantTypes: List<String> = listOf(),
             private val grantedAuthorities: List<String> = listOf()) : MongoObject(), ClientDetails {

    companion object {
        private const val serialVersionUID = -2918540686806255887L
    }

    @JsonIgnore
    override fun isSecretRequired(): Boolean {
        if (secret.isNullOrEmpty()) {
            return false
        }
        return secretRequired
    }

    @JsonIgnore
    override fun getAdditionalInformation(): MutableMap<String, Any> {
        return mutableMapOf()
    }

    @JsonGetter("accessTokenValidity")
    override fun getAccessTokenValiditySeconds(): Int {
        return accessTokenValidity
    }

    @JsonIgnore
    override fun getResourceIds(): MutableSet<String> {
        return resources.toMutableSet()
    }

    @JsonIgnore
    override fun getClientId(): String {
        return id
    }

    @JsonIgnore
    override fun isAutoApprove(scope: String?): Boolean {
        return autoApprove
    }

    @JsonIgnore
    override fun getAuthorities(): List<GrantedAuthority> {
        return grantedAuthorities.map { CustomSimpleGrantedAuthority(it) }
    }

    @JsonGetter("refreshTokenValidity")
    override fun getRefreshTokenValiditySeconds(): Int {
        return refreshTokenValidity
    }

    @JsonIgnore
    override fun getClientSecret(): String? {
        return secret
    }

    @JsonGetter("redirectUris")
    override fun getRegisteredRedirectUri(): MutableSet<String> {
        return redirectUris.toMutableSet()
    }

    @JsonIgnore
    override fun isScoped(): Boolean {
        return getScope().isNotEmpty()
    }

    override fun getScope(): MutableSet<String> {
        return scope.toMutableSet()
    }

    @JsonIgnore
    override fun getAuthorizedGrantTypes(): MutableSet<String> {
        return grantTypes.toMutableSet()
    }
}