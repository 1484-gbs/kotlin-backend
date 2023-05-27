package com.example.demo.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("app.sftp")
class SftpConfig {
    lateinit var host: String
    var port: Int = 0
    lateinit var user: String
    lateinit var password: String
    lateinit var subdir: String
}

