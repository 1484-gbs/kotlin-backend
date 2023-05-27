package com.example.demo.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LoginResponse(
    val token: String? = null,
    val otpReqId: String? = null,
)