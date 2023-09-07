package com.example.demo.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LoginResponse(
    @JsonProperty("one_time_token")
    val onetimeToken: String? = null,
    @JsonProperty("otp_req_id")
    val otpReqId: String? = null,
    @JsonProperty("access_token")
    val accessToken: String? = null,
    @JsonProperty("refresh_token")
    val refreshToken: String? = null
)