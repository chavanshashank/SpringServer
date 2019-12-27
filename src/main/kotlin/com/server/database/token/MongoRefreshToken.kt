package com.server.database.token

import org.springframework.security.oauth2.common.OAuth2RefreshToken

class MongoRefreshToken(token: String, auth: String?, username: String?, clientId: String?) :
        MongoBaseToken(token, auth, username, clientId), OAuth2RefreshToken {

    override fun getValue(): String? {
        return token
    }
}