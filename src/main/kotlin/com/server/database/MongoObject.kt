package com.server.database

import org.springframework.data.annotation.Id

open class MongoObject {

    /** The MongoDB ObjectId in hex form */
    @Id
    lateinit var id: String
}