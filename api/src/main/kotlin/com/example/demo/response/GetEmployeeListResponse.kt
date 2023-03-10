package com.example.demo.response

import com.example.demo.entity.EmployeeList
import com.example.demo.type.DateFormatType
import com.example.demo.utils.NumberUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.format.DateTimeFormatter

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GetEmployeeListResponse(
    val employees: List<GetEmployeeList>
) {
    data class GetEmployeeList(
        @JsonProperty("employee_id")
        val employeeId: Long,
        val name: String,
        val gender: String,
        val birthday: String,
        val age: Int,
        @JsonProperty("position_name")
        val positionName: String,
    ) {
        companion object {
            fun of(entity: EmployeeList): GetEmployeeList {
                return GetEmployeeList(
                    employeeId = entity.employeeId,
                    name = "${entity.lastName} ${entity.firstName}",
                    gender = entity.gender.display,
                    birthday = entity.birthday.format(DateTimeFormatter.ofPattern(DateFormatType.YYYY_MM_DD.value)),
                    age = NumberUtil.getAge(entity.birthday),
                    positionName = entity.positionName,
                )
            }
        }
    }
}
