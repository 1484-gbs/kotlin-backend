package com.example.demo.entity

import java.time.LocalDateTime

data class EmployeeSalary(
    val employeeSalaryId: Long,
    val employeeId: Long,
    val year: Int,
    val month: Int,
    val salaryOfMonth: Int,
    val healthInsurance: Int,
    val employmentIncrease: Int,
    val welfarePension: Int,
    val incomeTax: Int,
    val salaryPaid: Int,
    val createdBy: String,
    val createdAt: LocalDateTime,
    val updatedBy: String,
    val updatedAt: LocalDateTime,
)

