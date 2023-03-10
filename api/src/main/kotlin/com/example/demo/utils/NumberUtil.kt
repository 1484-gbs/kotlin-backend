package com.example.demo.utils

import java.time.LocalDate
import java.time.temporal.ChronoUnit

class NumberUtil {
    companion object {
        fun getAge(birthday: LocalDate): Int {
            return ChronoUnit.YEARS.between(birthday, LocalDate.now()).toInt()
        }
    }
}