package com.example.demo.entity

import java.time.LocalDateTime

data class OneTimeToken(
    val employeeId: Long,
    val oneTimeToken: String,
    val otpReqId: String,
    val expiredAt: LocalDateTime,
)