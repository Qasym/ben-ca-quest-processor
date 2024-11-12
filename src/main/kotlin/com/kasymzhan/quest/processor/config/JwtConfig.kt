package com.kasymzhan.quest.processor.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "jwt")
class JwtConfig {
    lateinit var secret: String
    var expiration: Long = 0
    lateinit var tokenPrefix: String
    lateinit var headerString: String
}