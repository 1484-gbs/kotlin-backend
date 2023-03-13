package com.example.demo.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("app.rate")
class RateConfig {
    lateinit var employmentIncrease: String
    lateinit var welfareIncrease: String
}
