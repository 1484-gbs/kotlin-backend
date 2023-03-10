package com.example.demo.request

import com.example.demo.type.GenderType
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class CreateEmployeeRequest(
    @JsonProperty("first_name")
    val firstName: String,
    @JsonProperty("last_name")
    val lastName: String,
    @JsonProperty("first_name_kana")
    val firstNameKana: String,
    @JsonProperty("last_name_kana")
    val lastNameKana: String,
    @JsonProperty("birthday")
    val birthDay: Birthday,
    @JsonProperty("gender")
    val gender: GenderType,
    @JsonProperty("tel")
    val tel: String?,
    @JsonProperty("position_id")
    val positionId: Long,
    @JsonProperty("skills")
    val skills: List<Long>
) {
    data class Birthday(
        val year: Int,
        val month: Int,
        val day: Int,
    ) {
        companion object {
            fun getLocalDate(birthDay: Birthday): LocalDate {
                return LocalDate.of(birthDay.year, birthDay.month, birthDay.day)
            }
        }
    }
}
