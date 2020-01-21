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
import java.time.LocalDateTime

interface BaseTokenRepository<T : MongoBaseToken> {
    /**
     * Retrieves a token from the database by it's token value.
     * @param token: The token value to search for. This is internally encrypted.
     * @return The token with the provided value.
     */
    fun findByToken(token: String?): T?

    /**
     * Retrieves tokens from the database by their client id and username.
     * @param clientId: The client id to search for.
     * @param username: The username to search for.
     * @return A list of tokens sharing the same client id and username.
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
    /**
     * Retrieves tokens from the database that share the provided client id.
     * @param clientId: The client id to search for.
     * @return A list of tokens sharing the same client id.
     */
    fun findByClientId(clientId: String?): List<MongoAccessToken>

    /**
     * Retrieves a token from the database with the provided authentication id.
     * @param authId: The authentication id to search for.
     * @return The token with the provided authentication id.
     */
    fun findByAuthenticationId(authId: String?): MongoAccessToken?

    /**
     * Removes all tokens with the provided refresh token value.
     * @param refreshToken: The refresh token value to search for.
     */
    fun deleteByRefreshToken(refreshToken: String?)

    /**
     * Removes all expired tokens with the provided refresh token value.
     * @param refreshToken: The refresh token value to search for.
     */
    fun deleteExpiredByRefreshToken(refreshToken: String?)
}

abstract class BaseTokenRepositoryImpl<T : MongoBaseToken>(protected val clazz: Class<T>) : AbstractMongoEventListener<T>(), BaseTokenRepository<T> {

    @Autowired
    protected lateinit var mongoTemplate: MongoTemplate

    @Autowired
    protected lateinit var crypto: ServerCrypto

    companion object {
        protected const val tokenKey = "token"
        protected const val encryptedTokenKey = "encryptedToken"
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
        // as an additional layer of security, token values are encrypted before storage
        if (event.document?.getBoolean(encryptedTokenKey) != true) {
            event.document?.set(tokenKey, crypto.encrypt(event.source.value))
            event.document?.set(encryptedTokenKey, true)
        }
    }

    override fun onAfterLoad(event: AfterLoadEvent<T>) {
        super.onAfterLoad(event)
        // decrypt token value after load
        if (event.document?.getBoolean(encryptedTokenKey) == true) {
            event.document?.set(tokenKey, crypto.decrypt(event.source.getString(tokenKey)))
            event.document?.set(encryptedTokenKey, false)
        }
    }
}

class AccessTokenRepositoryImpl : BaseTokenRepositoryImpl<MongoAccessToken>(MongoAccessToken::class.java), CustomAccessTokenRepository {

    companion object {
        private const val refreshTokenKey = "refreshToken"
        private const val encryptedRefreshTokenKey = "encryptedRefreshToken"
    }

    override fun onBeforeSave(event: BeforeSaveEvent<MongoAccessToken>) {
        super.onBeforeSave(event)

        if (event.document?.getBoolean(encryptedRefreshTokenKey) != true) {
            event.document?.set(refreshTokenKey, crypto.encrypt(event.source.refreshToken))
            event.document?.set(encryptedRefreshTokenKey, true)
        }
    }

    override fun onAfterLoad(event: AfterLoadEvent<MongoAccessToken>) {
        super.onAfterLoad(event)
        if (event.document?.getBoolean(encryptedRefreshTokenKey) == true) {
            event.document?.set(refreshTokenKey, crypto.decrypt(event.source.getString(refreshTokenKey)))
            event.document?.set(encryptedRefreshTokenKey, false)
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

    override fun deleteExpiredByRefreshToken(refreshToken: String?) {
        val q = Query()
        val now = LocalDateTime.now()
        q.addCriteria(Criteria.where(refreshTokenKey).isEqualTo(crypto.encrypt(refreshToken)).and("expiration").lte(now))
        mongoTemplate.remove(q, clazz)
    }
}

class RefreshTokenRepositoryImpl : BaseTokenRepositoryImpl<MongoRefreshToken>(MongoRefreshToken::class.java), CustomRefreshTokenRepository