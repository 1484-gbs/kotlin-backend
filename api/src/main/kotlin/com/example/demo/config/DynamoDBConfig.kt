package com.example.demo.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.aws.dynamo-db")
class DynamoDBConfig {
    lateinit var url: String
    lateinit var accessKey: String
    lateinit var secretKey: String
}