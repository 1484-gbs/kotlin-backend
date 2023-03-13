package com.example.demo.entity

import com.example.demo.type.IncreaseType
import java.math.BigDecimal

data class Increase(
    val increaseId: Long,
    val increaseType: IncreaseType,
    val salary: Int,
    val tax: BigDecimal,
    val halfTax: BigDecimal,
)

