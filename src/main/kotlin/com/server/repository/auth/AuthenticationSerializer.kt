package com.server.repository.auth

import org.apache.commons.codec.binary.Base64
import org.springframework.security.oauth2.common.util.SerializationUtils
import org.springframework.security.oauth2.provider.OAuth2Authentication

/**
 * OAuth2Authentication serializer for database storage.
 */
object AuthenticationSerializer {
    fun serialize(`object`: OAuth2Authentication?): String? {
        return try {
            val bytes = SerializationUtils.serialize(`object`)
            Base64.encodeBase64String(bytes)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deserialize(encodedObject: String?): OAuth2Authentication? {
        return try {
            val bytes = Base64.decodeBase64(encodedObject)
            SerializationUtils.deserialize<Any>(bytes) as OAuth2Authentication
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}