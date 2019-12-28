package com.server.resources

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

abstract class ResourceBase {

    /**
     * Returns the authentication object of the currently authenticated user.
     */
    val auth: Authentication?
        get() = SecurityContextHolder.getContext().authentication

    /**
     * Retrieves the username (usually email) of the currently authenticated user (according to the used access token)
     * from the security context.
     *
     * @return The username of the currently authenticated user
     */
    val currentUsername: String?
        get() = auth?.name
}