package com.example.demo.response

import com.example.demo.entity.EmployeeAndSkill
import com.example.demo.type.DateFormatType
import com.example.demo.utils.NumberUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.format.DateTimeFormatter

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GetEmployeeResponse(
    @JsonProperty("first_name")
    val firstName: String,
    @JsonProperty("last_name")
    val lastName: String,
    @JsonProperty("first_name_kana")
    val firstNameKana: String,
    @JsonProperty("last_name_kana")
    val lastNameKana: String,
    val birthday: String,
    val age: Int,
    val gender: String,
    val tel: String?,
    @JsonProperty("position_id")
    val positionId: Long,
    val skills: List<Long>
) {
    companion object {
        fun of(entity: EmployeeAndSkill): GetEmployeeResponse {
            return GetEmployeeResponse(
                firstName = entity.firstName,
                lastName = entity.lastName,
                firstNameKana = entity.firstNameKana,
                lastNameKana = entity.lastNameKana,
                birthday = entity.birthday.format(DateTimeFormatter.ofPattern(DateFormatType.YYYY_MM_DD.value)),
                age = NumberUtil.getAge(entity.birthday),
                gender = entity.gender.display,
                tel = entity.tel,
                positionId = entity.positionId,
                skills = entity.skills
            )
        }
    }
}
