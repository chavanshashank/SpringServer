package com.server.repository.token

import com.server.MyActiveProfile
import com.server.repository.auth.AuthenticationSerializer
import com.server.repository.auth.token.MongoAccessToken
import com.server.repository.auth.token.MongoRefreshToken
import com.server.repository.user.User
import com.server.util.TestCreator.createAuthentication
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest
@MyActiveProfile
abstract class BaseTokenRepositoryTest {

    protected val username = "username"
    protected val clientId = "clientId"
    protected val authId = "authId"

    protected fun createAccessToken(token: String = "at", refreshToken: String = "rt"): MongoAccessToken {
        val user = User(username, "pw")
        val date = Date()
        val authObject = createAuthentication(user, clientId)
        return MongoAccessToken(token, refreshToken, AuthenticationSerializer.serialize(authObject), authId, date, "bearer", mutableSetOf("app"), null, username, clientId)
    }

    protected fun createRefreshToken(token: String = "rt"): MongoRefreshToken {
        val user = User(username, "pw")
        val authObject = createAuthentication(user, clientId)
        return MongoRefreshToken(token, AuthenticationSerializer.serialize(authObject), username, clientId)
    }
}