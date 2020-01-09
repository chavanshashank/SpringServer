package com.server.repository.user

import com.fasterxml.jackson.annotation.JsonIgnore
import com.server.auth.CustomSimpleGrantedAuthority
import com.server.repository.MongoObject
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class User(@get:JvmName("username_") var username: String, @get:JvmName("password_") var password: String) : MongoObject(), UserDetails {

    companion object {
        private const val serialVersionUID = -435768273782348762L
    }

    var enabled: Boolean = true

    @JsonIgnore
    var grantedAuthorities: List<String> = listOf()

    @JsonIgnore
    override fun getAuthorities(): List<GrantedAuthority> {
        return grantedAuthorities.map { CustomSimpleGrantedAuthority(it) }
    }

    override fun isEnabled(): Boolean {
        return enabled
    }

    override fun getUsername(): String {
        return username
    }

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun getPassword(): String {
        return password
    }

    @JsonIgnore
    override fun isAccountNonExpired(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (other is User) {
            return username == other.username && password == other.password && enabled == other.enabled && grantedAuthorities == other.grantedAuthorities
        }
        return false
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + enabled.hashCode()
        result = 31 * result + grantedAuthorities.hashCode()
        return result
    }
}