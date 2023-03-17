package com.example.demo.config

import com.example.demo.filter.JwtFilter
import com.example.demo.filter.RoleFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtFilter: JwtFilter,
    private val roleFilter: RoleFilter) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf()
            .disable()
            .addFilterBefore(
                jwtFilter, UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterAfter(
                roleFilter, JwtFilter::class.java
            )
        return http.build()
    }
}