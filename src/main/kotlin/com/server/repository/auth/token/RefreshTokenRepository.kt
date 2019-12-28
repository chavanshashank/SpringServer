package com.server.repository.auth.token

import com.server.repository.MongoRepositoryBase
import com.server.repository.auth.token.CustomRefreshTokenRepository
import com.server.repository.auth.token.MongoRefreshToken

interface RefreshTokenRepository : MongoRepositoryBase<MongoRefreshToken, String>, CustomRefreshTokenRepository

