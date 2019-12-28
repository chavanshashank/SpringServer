package com.server.repository.auth.code

import com.server.repository.MongoObject
import com.server.repository.auth.AuthenticationSerializer
import org.springframework.security.oauth2.provider.OAuth2Authentication

class AuthorizationCodeObject(val code: String, private val auth: String) : MongoObject() {

    val authentication: OAuth2Authentication?
        get() = AuthenticationSerializer.deserialize(auth)
}