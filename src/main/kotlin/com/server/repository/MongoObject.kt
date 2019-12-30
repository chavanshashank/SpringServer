package com.server.repository

import org.springframework.data.annotation.Id

/**
 * Baseclass of all objects stored in MongoDB.
 */
abstract class MongoObject {

    /** The MongoDB ObjectId as a hex String */
    @Id
    lateinit var id: String
}