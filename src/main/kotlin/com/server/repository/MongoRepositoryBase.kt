package com.server.repository

import org.springframework.data.mongodb.repository.MongoRepository

interface MongoRepositoryBase<T: MongoObject, S>: MongoRepository<T, S>