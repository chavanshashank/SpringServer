package com.server.repository.user

import com.server.repository.MongoRepositoryBase

interface UserRepository: MongoRepositoryBase<User, String> {
    /**
     * Searches for a user with the provided username.
     * @param username: The username to search for.
     */
    fun findByUsername(username: String?): User?
}