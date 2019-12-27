package com.server.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter


@Configuration
@EnableResourceServer
class ResourceServerConfig : ResourceServerConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.requestMatchers().antMatchers("/me").and()
                .requestMatchers().antMatchers("/api/**").and()
                .authorizeRequests().antMatchers("/api/test").permitAll().and()
                .authorizeRequests().anyRequest().authenticated();
    }
}