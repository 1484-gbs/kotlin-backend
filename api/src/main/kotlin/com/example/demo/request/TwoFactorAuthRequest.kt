package com.example.demo.request

import com.fasterxml.jackson.annotation.JsonProperty

data class TwoFactorAuthRequest(
    @JsonProperty("login_id")
    val loginId: String,
    @JsonProperty("one_time_token")
    val oneTimeToken: String,
    @JsonProperty("otp_req_id")
    val otpReqId: String

)