package com.example.demo.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GetAddressResponse(
    val message: String? = null,
    val results: List<Address>?
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class Address(
        val zipcode: String,
        val prefcode: String,
        val address1: String,
        val address2: String,
        val address3: String?,
    )
}