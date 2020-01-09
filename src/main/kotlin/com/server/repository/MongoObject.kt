package com.server.repository

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSetter
import org.springframework.data.annotation.Id

/**
 * Baseclass of all objects stored in MongoDB.
 */
abstract class MongoObject {

    /** The MongoDB ObjectId as a hex String set by the database. This property is not de-serialized (ignored) when
     * sent to the server but serialized when sent from the server to a client. */
    @get:JsonIgnore
    @set:JsonSetter
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Id
    lateinit var id: String
}