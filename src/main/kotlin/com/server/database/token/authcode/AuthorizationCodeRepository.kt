package com.server.database.token.authcode

import com.server.database.MongoRepositoryBase

interface AuthorizationCodeRepository : MongoRepositoryBase<AuthorizationCodeObject, String> {
    fun findByCode(code: String?): AuthorizationCodeObject?
}