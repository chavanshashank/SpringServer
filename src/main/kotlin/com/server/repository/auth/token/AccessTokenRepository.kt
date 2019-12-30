package com.server.repository.auth.token

import com.server.repository.MongoRepositoryBase

interface AccessTokenRepository : MongoRepositoryBase<MongoAccessToken>, CustomAccessTokenRepository

