package com.server.database.token.authcode

import com.server.database.MongoObject
import com.server.database.token.AuthenticationSerializer
import org.springframework.security.oauth2.provider.OAuth2Authentication

class AuthorizationCodeObject(val code: String, private val auth: String) : MongoObject() {

    val authentication: OAuth2Authentication?
        get() = AuthenticationSerializer.deserialize(auth)
}