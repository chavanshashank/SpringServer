package com.server.repository.auth.code

import com.server.repository.MongoRepositoryBase

interface AuthorizationCodeRepository : MongoRepositoryBase<AuthorizationCode> {
    fun findByCode(code: String?): AuthorizationCode?
}