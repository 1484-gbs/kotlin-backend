package com.example.demo.request

import com.example.demo.annotation.Name
import com.example.demo.annotation.NameKana
import com.example.demo.annotation.SalaryOfMonth
import com.example.demo.annotation.Tel
import com.fasterxml.jackson.annotation.JsonProperty

data class PatchEmployeeRequest(
    @JsonProperty("first_name")
    @field:Name
    val firstName: String,
    @JsonProperty("last_name")
    @field:Name
    val lastName: String,
    @JsonProperty("first_name_kana")
    @field:NameKana
    val firstNameKana: String,
    @JsonProperty("last_name_kana")
    @field:NameKana
    val lastNameKana: String,
    @JsonProperty("tel")
    @field:Tel
    val tel: String?,
    @JsonProperty("position_id")
    val positionId: Long,
    @JsonProperty("skills")
    val skills: List<Long>,
    @JsonProperty("photo")
    val photo: String?,
    @JsonProperty("is_delete_photo")
    val isDeletePhoto: Boolean? = false,
    @JsonProperty("salary_of_month")
    @field:SalaryOfMonth
    val salaryOfMonth: Int,
)
