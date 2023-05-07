package com.example.demo.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.integration.file.remote.session.CachingSessionFactory
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate
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

