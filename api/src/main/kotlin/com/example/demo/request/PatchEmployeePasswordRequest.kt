package com.example.demo.request

import com.fasterxml.jackson.annotation.JsonProperty

data class PatchEmployeePasswordRequest(
    @JsonProperty("password")
    val password: String,
)
