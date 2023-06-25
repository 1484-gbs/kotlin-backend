package com.example.demo.entity

data class EmployeeTwoFactorAuth(
    val employeeId: Long,
    val loginId: String,
    val tokenId: String,
    val oneTimeToken: String,
)
