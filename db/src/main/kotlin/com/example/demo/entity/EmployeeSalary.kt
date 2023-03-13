package com.example.demo.entity

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
    val salaryPaid: Int
)

