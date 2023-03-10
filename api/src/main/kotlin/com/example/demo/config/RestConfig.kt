package com.example.demo.config

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate

@Configuration
class RestConfig{
    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        val messageConverter = MappingJackson2HttpMessageConverter()
        messageConverter.supportedMediaTypes = listOf(MediaType.TEXT_PLAIN)
        return builder.additionalMessageConverters(messageConverter).build()
    }
}