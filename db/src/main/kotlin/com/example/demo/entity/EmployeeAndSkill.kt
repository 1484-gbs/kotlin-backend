package com.example.demo.entity

import com.example.demo.type.GenderType
import java.time.LocalDate

// mybatisでcollection使用時、data class`だとエラーになる
class EmployeeAndSkill{
    var employeeId: Long = 0
    lateinit var firstName: String
    lateinit var lastName: String
    lateinit var firstNameKana: String
    lateinit var lastNameKana: String
    lateinit var birthday: LocalDate
    lateinit var gender: GenderType
    var tel: String? = null
    var positionId: Long = 0
    var skills: List<Long> = mutableListOf()
    var salaryOfMonth: Int = 0
    lateinit var loginId: String
}

