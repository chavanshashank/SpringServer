package com.server.database

import org.springframework.data.mongodb.repository.MongoRepository

interface MongoRepositoryBase<T: MongoObject, S>: MongoRepository<T, S>