package com.example.demo.client

import com.example.demo.config.SftpConfig
import org.springframework.integration.file.remote.session.CachingSessionFactory
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate

class SftpClientBuilder {
    companion object {
        fun build(config: SftpConfig): SftpRemoteFileTemplate {
            val factory = CachingSessionFactory(
                DefaultSftpSessionFactory(true).apply {
                    this.setHost(config.host)
                    this.setPort(config.port)
                    this.setUser(config.user)
                    this.setPassword(config.password)
                    this.setAllowUnknownKeys(true)
                }
            )
            return SftpRemoteFileTemplate(factory)
        }
    }
}