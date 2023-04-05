package com.example.demo.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.role")
class RoleConfig {
    lateinit var denyUrl: DenyUrls

    data class DenyUrls(
        val admin: List<DenyUrl>,
        val manager: List<DenyUrl>,
        val general: List<DenyUrl>,
    ) {
        data class DenyUrl(
            val method: String,
            val url: String
        )
    }
}