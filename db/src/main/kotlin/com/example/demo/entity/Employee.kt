package com.example.demo.entity

import com.example.demo.type.GenderType
import java.time.LocalDate

data class Employee(
    val employeeId: Long,
    val firstName: String,
    val lastName: String,
    val firstNameKana: String,
    val lastNameKana: String,
    val birthday: LocalDate,
    val gender: GenderType,
    val tel: String?,
    val positionId: Long,
    val salaryOfMonth: Int
)

