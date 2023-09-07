package com.example.demo.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.jwt")
class JwtConfig {
    var accessToken: AccessToken = AccessToken()
    var refreshToken: RefreshToken = RefreshToken()
    lateinit var secret: String
    lateinit var aud: String
    lateinit var sub: String

    class AccessToken {
        var expiredMinutes: Long = 0
    }

    class RefreshToken {
        var expiredMinutes: Long = 0
        var length: Int = 0
    }
}