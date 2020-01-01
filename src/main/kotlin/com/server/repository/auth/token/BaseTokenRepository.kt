package com.server.repository.auth.token

import com.server.crypto.ServerCrypto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.AfterLoadEvent
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo

interface BaseTokenRepository<T : MongoBaseToken> {
    /**
     * Retrieves a token from the database by it's token value.
     * @param token: The token value to search for. This is internally encrypted.
     */
    fun findByToken(token: String?): T?

    /**
     * Retrieves tokens from the database by their client id and username.
     * @param clientId: The client id to search for.
     * @param username: The username to search for.
     */
    fun findByClientIdAndUsername(clientId: String?, username: String?): List<T>

    /**
     * Removes a token from the database by it's token value.
     * @param token: The token value to remove. This is internally encrypted.
     */
    fun deleteByToken(token: String?)
}

interface CustomRefreshTokenRepository : BaseTokenRepository<MongoRefreshToken>

interface CustomAccessTokenRepository : BaseTokenRepository<MongoAccessToken> {
    fun findByClientId(clientId: String?): List<MongoAccessToken>
    fun findByAuthenticationId(authId: String?): MongoAccessToken?
    fun deleteByRefreshToken(refreshToken: String?)
}

abstract class BaseTokenRepositoryImpl<T : MongoBaseToken>(protected val clazz: Class<T>) : AbstractMongoEventListener<T>(), BaseTokenRepository<T> {

    @Autowired
    protected lateinit var mongoTemplate: MongoTemplate

    @Autowired
    protected lateinit var crypto: ServerCrypto

    companion object {
        protected const val tokenKey = "token"
        protected const val encryptedTokenValueKey = "encryptedTokenValue"
    }

    override fun findByToken(token: String?): T? {
        val q = Query()
        q.addCriteria(Criteria.where(tokenKey).isEqualTo(crypto.encrypt(token)))
        return mongoTemplate.findOne(q, clazz)
    }

    override fun findByClientIdAndUsername(clientId: String?, username: String?): List<T> {
        val q = Query()
        q.addCriteria(Criteria.where("clientId").isEqualTo(clientId).and("username").isEqualTo(username))
        return mongoTemplate.find(q, clazz)
    }

    override fun deleteByToken(token: String?) {
        val q = Query()
        q.addCriteria(Criteria.where(tokenKey).isEqualTo(crypto.encrypt(token)))
        mongoTemplate.remove(q, clazz)
    }

    override fun onBeforeSave(event: BeforeSaveEvent<T>) {
        super.onBeforeSave(event)
        if (event.document?.getBoolean(encryptedTokenValueKey) != true) {
            event.document?.set(tokenKey, crypto.encrypt(event.source.token))
            event.document?.set(encryptedTokenValueKey, true)
        }
    }

    override fun onAfterLoad(event: AfterLoadEvent<T>) {
        super.onAfterLoad(event)
        if (event.document?.getBoolean(encryptedTokenValueKey) != false) {
            event.document?.set(tokenKey, crypto.decrypt(event.source.getString(tokenKey)))
            event.document?.set(encryptedTokenValueKey, false)
        }
    }
}

class AccessTokenRepositoryImpl : BaseTokenRepositoryImpl<MongoAccessToken>(MongoAccessToken::class.java), CustomAccessTokenRepository {

    companion object {
        private const val refreshTokenKey = "refreshTokenValue"
        private const val encryptedRefreshTokenValueKey = "encryptedRefreshTokenValue"
    }

    override fun onBeforeSave(event: BeforeSaveEvent<MongoAccessToken>) {
        super.onBeforeSave(event)

        if (event.document?.getBoolean(encryptedRefreshTokenValueKey) != true) {
            event.document?.set(refreshTokenKey, crypto.encrypt(event.source.refreshToken?.value))
            event.document?.set(encryptedRefreshTokenValueKey, true)
        }
    }

    override fun onAfterLoad(event: AfterLoadEvent<MongoAccessToken>) {
        super.onAfterLoad(event)
        if (event.document?.getBoolean(encryptedRefreshTokenValueKey) != false) {
            event.document?.set(refreshTokenKey, crypto.decrypt(event.source.getString(refreshTokenKey)))
            event.document?.set(encryptedRefreshTokenValueKey, false)
        }
    }

    override fun findByClientId(clientId: String?): List<MongoAccessToken> {
        val q = Query()
        q.addCriteria(Criteria.where("clientId").isEqualTo(clientId))
        return mongoTemplate.find(q, clazz)
    }

    override fun findByAuthenticationId(authId: String?): MongoAccessToken? {
        val q = Query()
        q.addCriteria(Criteria.where("authenticationId").isEqualTo(authId))
        return mongoTemplate.findOne(q, clazz)
    }

    override fun deleteByRefreshToken(refreshToken: String?) {
        val q = Query()
        q.addCriteria(Criteria.where(refreshTokenKey).isEqualTo(crypto.encrypt(refreshToken)))
        mongoTemplate.remove(q, clazz)
    }
}

class RefreshTokenRepositoryImpl : BaseTokenRepositoryImpl<MongoRefreshToken>(MongoRefreshToken::class.java), CustomRefreshTokenRepository