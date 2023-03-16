package com.example.demo.response

import com.example.demo.entity.Employee
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GetEmployeeRandomResponse(
    val employees: List<GetEmployeeRandom>
) {
    data class GetEmployeeRandom(
        @JsonProperty("employee_id")
        val employeeId: Long,
        val name: String,
    ) {
        companion object {
            fun of(entity: Employee): GetEmployeeRandom {
                return GetEmployeeRandom(
                    employeeId = entity.employeeId,
                    name = "${entity.lastName} ${entity.firstName}"
                )
            }
        }
    }
}
