package com.example.demo.dto

import com.example.demo.entity.EmployeeSalary
import com.opencsv.bean.CsvBindByPosition

data class EmployeeSalaryCSV(
    @field:CsvBindByPosition(position = 0)
    val employeeId: Long,
    @field:CsvBindByPosition(position = 1)
    val yearMonth: Int,
    @field:CsvBindByPosition(position = 2)
    val salaryOfMonth: Int,
    @field:CsvBindByPosition(position = 3)
    val healthInsurance: Int,
    @field:CsvBindByPosition(position = 4)
    val employmentIncrease: Int,
    @field:CsvBindByPosition(position = 5)
    val welfarePension: Int,
    @field:CsvBindByPosition(position = 6)
    val incomeTax: Int,
    @field:CsvBindByPosition(position = 7)
    val salaryPaid: Int,
) {
    companion object {
        fun of(entity: EmployeeSalary): EmployeeSalaryCSV {
            return EmployeeSalaryCSV(
                employeeId = entity.employeeId,
                yearMonth = "${entity.year}${entity.month}".toInt(),
                salaryOfMonth = entity.salaryOfMonth,
                healthInsurance = entity.healthInsurance,
                employmentIncrease = entity.employmentIncrease,
                welfarePension = entity.welfarePension,
                incomeTax = entity.incomeTax,
                salaryPaid = entity.salaryPaid
            )
        }
    }
}

