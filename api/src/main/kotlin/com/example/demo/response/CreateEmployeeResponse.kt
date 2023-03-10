package com.example.demo.response

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateEmployeeResponse(
    @JsonProperty("employee_id")
    val employeeId: Long,
)
