package com.example.demo.entity

import com.example.demo.type.RoleType

data class Position(
    val positionId: Long,
    val positionName: String,
    val role: RoleType,
    val displayOrder: Int,
)
