package com.example.demo.usecase

import com.example.demo.response.GetAddressResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

interface GetAddressUseCase {
    fun execute(zipcode: String): GetAddressResponse
}

@Service
class GetAddressUseCaseCaseImpl(
    private val restTemplate: RestTemplate
) : GetAddressUseCase {

    @Value("\${app.postal-code.url}")
    private lateinit var url: String

    override fun execute(zipcode: String): GetAddressResponse {
        return restTemplate.getForObject(
            UriComponentsBuilder.fromUriString(url).queryParam("zipcode", zipcode).toUriString(),
            GetAddressResponse::class.java
        ) ?: GetAddressResponse(results = listOf())
    }
}