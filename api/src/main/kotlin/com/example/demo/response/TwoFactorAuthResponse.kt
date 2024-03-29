package com.example.demo.response

import com.fasterxml.jackson.annotation.JsonProperty

data class TwoFactorAuthResponse(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("refresh_token")
    val refreshToken: String
)