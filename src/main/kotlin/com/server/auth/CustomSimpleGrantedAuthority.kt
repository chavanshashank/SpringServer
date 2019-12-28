package com.server.auth

import org.springframework.security.core.GrantedAuthority
import java.io.Serializable

/**
 * Custom "SimpleGrantedAuthority" to avoid serialization issues.
 */
class CustomSimpleGrantedAuthority(private val role: String) : GrantedAuthority, Serializable {

    companion object {
        private const val serialVersionUID = 110L
    }

    override fun getAuthority(): String {
        return role
    }
}