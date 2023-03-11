package com.example.demo.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.aws.s3")
class S3Config {
    lateinit var url: String
    lateinit var bucket: String
    lateinit var accessKey: String
    lateinit var secretKey: String
}