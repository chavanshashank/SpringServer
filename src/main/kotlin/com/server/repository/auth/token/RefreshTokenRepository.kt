package com.server.repository.auth.token

import com.server.repository.MongoRepositoryBase

interface RefreshTokenRepository : MongoRepositoryBase<MongoRefreshToken>, CustomRefreshTokenRepository

