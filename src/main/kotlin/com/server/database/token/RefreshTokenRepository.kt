package com.server.database.token

import com.server.database.MongoRepositoryBase

interface RefreshTokenRepository : MongoRepositoryBase<MongoRefreshToken, String>, CustomRefreshTokenRepository

