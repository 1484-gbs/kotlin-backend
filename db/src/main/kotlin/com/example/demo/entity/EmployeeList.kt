package com.example.demo.entity

import com.example.demo.type.GenderType
import java.time.LocalDate

data class EmployeeList(
    val employeeId: Long,
    val firstName: String,
    val lastName: String,
    val birthday: LocalDate,
    val gender: GenderType,
    val positionName: String,
)
