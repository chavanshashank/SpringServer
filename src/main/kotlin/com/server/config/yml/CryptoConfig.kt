package com.server.config.yml

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("crypto")
class CryptoConfig {
    lateinit var key: String
}