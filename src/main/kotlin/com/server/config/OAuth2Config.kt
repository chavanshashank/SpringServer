package com.server.config

import com.server.auth.CustomAuthorizationCodeServices
import com.server.auth.CustomTokenServices
import com.server.auth.MongoTokenStore
import com.server.auth.clientdetails.OAuthClientDetailsService
import com.server.auth.userdetails.OAuthUserDetailsService
import com.server.repository.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.provider.CompositeTokenGranter
import org.springframework.security.oauth2.provider.TokenGranter
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter


@Configuration
@EnableAuthorizationServer
class OAuth2Config : AuthorizationServerConfigurerAdapter() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var mongoTokenStore: MongoTokenStore

    @Autowired
    private lateinit var clientDetailsService: OAuthClientDetailsService

    @Autowired
    private lateinit var userDetailsService: OAuthUserDetailsService

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var authorizationCodeServices: CustomAuthorizationCodeServices

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.withClientDetails(clientDetailsService)
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints.authorizationCodeServices(authorizationCodeServices).userDetailsService(userDetailsService)
                .tokenStore(mongoTokenStore).tokenServices(getTokenServices()).tokenGranter(tokenGranter(endpoints))
    }

    private fun tokenGranter(endpoints: AuthorizationServerEndpointsConfigurer): TokenGranter? {
        val granters: MutableList<TokenGranter> = mutableListOf()

        granters.add(AuthorizationCodeTokenGranter(endpoints.tokenServices, authorizationCodeServices,
                endpoints.clientDetailsService, endpoints.oAuth2RequestFactory))
        granters.add(RefreshTokenGranter(endpoints.tokenServices, endpoints.clientDetailsService,
                endpoints.oAuth2RequestFactory))
        granters.add(ClientCredentialsTokenGranter(endpoints.tokenServices,
                endpoints.clientDetailsService, endpoints.oAuth2RequestFactory))
        granters.add(ResourceOwnerPasswordTokenGranter(authenticationManager, endpoints.tokenServices,
                endpoints.clientDetailsService, endpoints.oAuth2RequestFactory))

        return CompositeTokenGranter(granters)
    }

    @Bean
    fun getTokenServices(): CustomTokenServices {
        val services = CustomTokenServices(userRepository)
        services.setTokenStore(mongoTokenStore)
        services.setClientDetailsService(clientDetailsService)
        services.setSupportRefreshToken(true)
        return services
    }
}