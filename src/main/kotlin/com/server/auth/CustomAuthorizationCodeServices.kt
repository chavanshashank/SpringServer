package com.server.auth

import com.server.database.token.AuthenticationSerializer
import com.server.database.token.authcode.AuthorizationCodeObject
import com.server.database.token.authcode.AuthorizationCodeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices

class CustomAuthorizationCodeServices : AuthorizationCodeServices {

    @Autowired
    private lateinit var authorizationCodeRepository: AuthorizationCodeRepository

    /** 48 byte random generator  */
    private val generator = RandomValueStringGenerator(48)

    override fun createAuthorizationCode(authentication: OAuth2Authentication): String? {
        val code = generator.generate()
        val serialized = AuthenticationSerializer.serialize(authentication)

        return if (serialized == null) {
            null
        } else {
            val authCodeObject = AuthorizationCodeObject(code, serialized)
            authorizationCodeRepository.save(authCodeObject)
            code
        }
    }

    @Throws(InvalidGrantException::class)
    override fun consumeAuthorizationCode(code: String): OAuth2Authentication? {
        val authCodeObject = authorizationCodeRepository.findByCode(code)
        return if (authCodeObject != null) {
            // "consume" the code by deleting the object
            authorizationCodeRepository.delete(authCodeObject)
            authCodeObject.authentication
        } else {
            throw InvalidGrantException("Authentication object for authorization code not found")
        }
    }
}
