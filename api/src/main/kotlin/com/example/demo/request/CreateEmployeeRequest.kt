package com.example.demo.request

import com.example.demo.annotation.Name
import com.example.demo.annotation.NameKana
import com.example.demo.annotation.SalaryOfMonth
import com.example.demo.annotation.Tel
import com.example.demo.type.GenderType
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.time.LocalDate

data class CreateEmployeeRequest(
    @JsonProperty("first_name")
    @field:Name
    val firstName: String,
    @JsonProperty("last_name")
    @field:Name
    val lastName: String,
    @field:NameKana
    @JsonProperty("first_name_kana")
    val firstNameKana: String,
    @JsonProperty("last_name_kana")
    @field:NameKana
    val lastNameKana: String,
    @JsonProperty("birthday")
    @field:Valid
    val birthDay: Birthday,
    @JsonProperty("gender")
    val gender: GenderType,
    @JsonProperty("tel")
    @field:Tel
    val tel: String?,
    @JsonProperty("position_id")
    val positionId: Long,
    @JsonProperty("skills")
    val skills: List<Long>,
    @JsonProperty("photo")
    val photo: String?,
    @JsonProperty("salary_of_month")
    @field:SalaryOfMonth
    val salaryOfMonth: Int,
) {
    data class Birthday(
        @field:Min(1000)
        @field:Max(9999)
        val year: Int,
        @field:Min(1)
        @field:Max(12)
        val month: Int,
        @field:Min(1)
        @field:Max(31)
        val day: Int,
    ) {
        companion object {
            fun getLocalDate(birthDay: Birthday): LocalDate {
                return LocalDate.of(birthDay.year, birthDay.month, birthDay.day)
            }
        }
    }
}
