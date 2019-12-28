package com.server.repository.auth.code

import com.server.repository.MongoRepositoryBase

interface AuthorizationCodeRepository : MongoRepositoryBase<AuthorizationCodeObject, String> {
    fun findByCode(code: String?): AuthorizationCodeObject?
}