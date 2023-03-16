package com.example.demo.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.jwt")
class JwtConfig {
    lateinit var expiredHour: String
    lateinit var secret: String
    lateinit var aud: String
    lateinit var sub: String
}