package com.example.demo.request

import com.fasterxml.jackson.annotation.JsonProperty

data class PatchEmployeeRequest(
    @JsonProperty("first_name")
    val firstName: String,
    @JsonProperty("last_name")
    val lastName: String,
    @JsonProperty("first_name_kana")
    val firstNameKana: String,
    @JsonProperty("last_name_kana")
    val lastNameKana: String,
    @JsonProperty("tel")
    val tel: String?,
    @JsonProperty("position_id")
    val positionId: Long,
    @JsonProperty("skills")
    val skills: List<Long>
)
