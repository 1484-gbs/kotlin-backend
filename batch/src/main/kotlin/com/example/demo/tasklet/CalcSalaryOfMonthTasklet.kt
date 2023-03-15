package com.example.demo.tasklet

import com.example.demo.config.RateConfig
import com.example.demo.entity.EmployeeSalary
import com.example.demo.repository.EmployeeMapper
import com.example.demo.repository.EmployeeSalaryMapper
import com.example.demo.repository.IncreaseMapper
import com.example.demo.type.IncreaseType
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime

@Component
@StepScope
class CalcSalaryOfMonthTasklet(
    private val employeeMapper: EmployeeMapper,
    private val employeeSalaryMapper: EmployeeSalaryMapper,
    private val increaseMapper: IncreaseMapper,
    private val rateConfig: RateConfig
) : Tasklet {

    companion object {
        val HALF: BigDecimal = BigDecimal.valueOf(0.5)
    }
    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {

        runCatching {
            val employees = employeeMapper.findAll()
            val increases = increaseMapper.findAll()
            val rateEmploymentIncrease = BigDecimal(rateConfig.employmentIncrease)
            val rateWelfareIncrease = BigDecimal(rateConfig.welfareIncrease)

            val employeeSalaries = employees.asSequence().map { e ->
                val salaryOfMonth = BigDecimal.valueOf(e.salaryOfMonth.toLong())
                // 雇用保険額
                val employmentIncrease =
                    salaryOfMonth.multiply(rateEmploymentIncrease)
                        .setScale(0, RoundingMode.DOWN)
                // 健康保険料
                val healthInsurance = increases.filter { it.increaseType == IncreaseType.HEALTH }
                    .first {
                        salaryOfMonth < BigDecimal.valueOf(it.salary.toLong())
                    }.halfTax.setScale(0, RoundingMode.DOWN)
                // 厚生年金
                val welfarePension = increases.filter { it.increaseType == IncreaseType.WELFARE }
                    .firstOrNull {
                        salaryOfMonth < BigDecimal.valueOf(it.salary.toLong())
                    }?.halfTax
                    ?: run {
                        // とりあえず一律
                        salaryOfMonth.multiply(rateWelfareIncrease).multiply(HALF)
                    }.setScale(0, RoundingMode.DOWN)

                // TODO 所得税、住民税 etc

                val salaryPaid = salaryOfMonth
                    .subtract(employmentIncrease)
                    .subtract(healthInsurance)
                    .subtract(welfarePension)

                val now = LocalDateTime.now()

                EmployeeSalary(
                    employeeSalaryId = 0,
                    employeeId = e.employeeId,
                    year = now.year,
                    month = now.monthValue,
                    salaryOfMonth = e.salaryOfMonth,
                    healthInsurance = healthInsurance.toInt(),
                    welfarePension = welfarePension.toInt(),
                    employmentIncrease = employmentIncrease.toInt(),
                    incomeTax = 0, // TODO
                    salaryPaid = salaryPaid.toInt(),
                    createdBy = "loginId",
                    createdAt = now,
                    updatedBy = "loginId",
                    updatedAt = now,
                )
            }.toList()

            employeeSalaryMapper.upsert(employeeSalaries)

        }.fold(
            onSuccess = {
                return RepeatStatus.FINISHED
            },
            onFailure = {
                throw RuntimeException("failed to calcSalaryOfMonth.", it)
            }
        )
    }
}