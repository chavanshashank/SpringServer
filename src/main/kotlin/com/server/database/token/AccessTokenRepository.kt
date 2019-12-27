package com.server.database.token

import com.server.database.MongoRepositoryBase

interface AccessTokenRepository : MongoRepositoryBase<MongoAccessToken, String>, CustomAccessTokenRepository

