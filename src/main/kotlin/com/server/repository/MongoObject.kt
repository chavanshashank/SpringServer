package com.server.repository

import org.springframework.data.annotation.Id

/**
 * Baseclass of all objects stored in MongoDB.
 */
open class MongoObject {

    /** The MongoDB ObjectId in hex form */
    @Id
    lateinit var id: String
}