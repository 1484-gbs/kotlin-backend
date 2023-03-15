package com.example.demo.request

import com.fasterxml.jackson.annotation.JsonProperty

data class LoginRequest(
    @JsonProperty("login_id")
    val loginId: String,
    @JsonProperty("password")
    val password: String
)