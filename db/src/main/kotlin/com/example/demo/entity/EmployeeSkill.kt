package com.example.demo.entity

import java.time.LocalDateTime

data class EmployeeSkill(
    val employeeSkillId: Long,
    val employeeId: Long,
    val skillId: Long,
    val lastModifiedBy: String,
    val lastModifiedAt: LocalDateTime,
)

