package com.example.demo.entity

import com.example.demo.type.GenderType
import java.time.LocalDate
import java.time.LocalDateTime

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
    val salaryOfMonth: Int,
    val loginId: String,
    val password: String,
    val createdBy: String,
    val createdAt: LocalDateTime,
    val updatedBy: String?,
    val updatedAt: LocalDateTime?,
)

