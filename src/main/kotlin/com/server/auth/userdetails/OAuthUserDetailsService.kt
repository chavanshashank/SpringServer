package com.server.auth.userdetails

import com.server.repository.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

class OAuthUserDetailsService : UserDetailsService {

    @Autowired
    private lateinit var userRepository: UserRepository

    override fun loadUserByUsername(username: String?): UserDetails {
        val user = userRepository.findByUsername(username)
        if (user == null) {
            // returning null from UserDetailsService is not allowed
            throw UsernameNotFoundException("No user found for username $username")
        } else {
            return user
        }
    }
}