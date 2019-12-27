package com.server.database.user

import com.server.auth.CustomSimpleGrantedAuthority
import com.server.database.MongoObject
import net.minidev.json.annotate.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class User(
        @get:JvmName("username_") val username: String, private val pw: String, private val enabled: Boolean = true, private val grantedAuthorities: List<String> = listOf()) : MongoObject(), UserDetails {

    override fun getAuthorities(): List<GrantedAuthority> {
        return grantedAuthorities.map { CustomSimpleGrantedAuthority(it) }
    }

    override fun isEnabled(): Boolean {
        return enabled
    }

    override fun getUsername(): String {
        return username
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun getPassword(): String {
        return pw
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }
}