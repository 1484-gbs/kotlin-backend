package com.example.demo.dto

import com.example.demo.entity.EmployeeList
import com.example.demo.type.DateFormatType
import com.example.demo.utils.NumberUtil
import com.opencsv.bean.CsvBindByName
import com.opencsv.bean.CsvBindByPosition
import java.time.format.DateTimeFormatter

data class EmployeeListCSV(
    @field:CsvBindByPosition(position = 0)
    @field:CsvBindByName(column = "社員ID", required = true)
    val employeeId: Long,
    @field:CsvBindByPosition(position = 1)
    @field:CsvBindByName(column = "氏名", required = true)
    val name: String,
    @field:CsvBindByPosition(position = 2)
    @field:CsvBindByName(column = "性別", required = true)
    val gender: String,
    @field:CsvBindByPosition(position = 3)
    @field:CsvBindByName(column = "生年月日", required = true)
    val birthday: String,
    @field:CsvBindByPosition(position = 4)
    @field:CsvBindByName(column = "年齢", required = true)
    val age: Int,
    @field:CsvBindByPosition(position = 5)
    @field:CsvBindByName(column = "役職", required = true)
    val positionName: String,
) {
    companion object {
        fun of(entity: EmployeeList): EmployeeListCSV {
            return EmployeeListCSV(
                employeeId = entity.employeeId,
                name = "${entity.lastName} ${entity.firstName}",
                gender = entity.gender.display,
                birthday = entity.birthday.format(DateTimeFormatter.ofPattern(DateFormatType.YYYY_MM_DD.value)),
                age = NumberUtil.getAge(entity.birthday),
                positionName = entity.positionName,
            )
        }
    }
}

