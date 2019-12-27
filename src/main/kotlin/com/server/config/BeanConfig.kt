package com.server.config

import com.server.auth.CustomAuthorizationCodeServices
import com.server.auth.MongoTokenStore
import com.server.auth.clientdetails.OAuthClientDetailsService
import com.server.auth.userdetails.OAuthUserDetailsService
import com.server.config.yml.CryptoConfig
import com.server.crypto.ServerCrypto
import com.server.database.token.AccessTokenRepositoryImpl
import com.server.database.token.RefreshTokenRepositoryImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices

@Configuration
class BeanConfig {

    @Autowired
    private lateinit var cryptoConfig: CryptoConfig

    @Bean
    fun getMongoTokenStore(): MongoTokenStore {
        return MongoTokenStore()
    }

    @Bean
    fun getServerCrypto(): ServerCrypto {
        return ServerCrypto(cryptoConfig.key)
    }

    @Bean
    fun getBaseAccessTokenRepositoryImpl(): AccessTokenRepositoryImpl {
        return AccessTokenRepositoryImpl()
    }

    @Bean
    fun getBaseRefreshTokenRepositoryImpl(): RefreshTokenRepositoryImpl {
        return RefreshTokenRepositoryImpl()
    }

    @Bean
    fun getOAuthUserDetailsService(): OAuthUserDetailsService {
        return OAuthUserDetailsService()
    }

    @Bean
    fun getOAuthClientDetailsService(): OAuthClientDetailsService {
        return OAuthClientDetailsService()
    }

    @Bean
    fun getCustomAuthorizationCodeServices(): CustomAuthorizationCodeServices {
        return CustomAuthorizationCodeServices()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(4)
    }
}