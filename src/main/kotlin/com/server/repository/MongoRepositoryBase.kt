package com.server.repository

import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Baseclass of all MongoDB repositories.
 */
interface MongoRepositoryBase<T : MongoObject> : MongoRepository<T, String>