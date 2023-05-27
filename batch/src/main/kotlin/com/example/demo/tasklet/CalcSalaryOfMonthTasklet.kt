package com.example.demo.tasklet

import com.example.demo.client.SftpClientBuilder
import com.example.demo.config.RateConfig
import com.example.demo.config.SftpConfig
import com.example.demo.dto.EmployeeSalaryCSV
import com.example.demo.entity.EmployeeSalary
import com.example.demo.repository.EmployeeMapper
import com.example.demo.repository.EmployeeSalaryMapper
import com.example.demo.repository.IncreaseMapper
import com.example.demo.type.IncreaseType
import com.opencsv.bean.StatefulBeanToCsvBuilder
import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.expression.common.LiteralExpression
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import java.io.FileWriter
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.YearMonth


@Component
@StepScope
class CalcSalaryOfMonthTasklet(
    private val employeeMapper: EmployeeMapper,
    private val employeeSalaryMapper: EmployeeSalaryMapper,
    private val increaseMapper: IncreaseMapper,
    private val rateConfig: RateConfig,
    private val sftpConfig: SftpConfig,
) : Tasklet {

    @Value("#{jobParameters[loginId]}")
    lateinit var loginId: String

    @Value("#{jobParameters[targetYearMonth]}")
    var yearMonth: YearMonth? = null

    companion object {
        val HALF: BigDecimal = BigDecimal.valueOf(0.5)
    }

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {

        runCatching {
            val employees = employeeMapper.findAll()
            val increases = increaseMapper.findAll()
            val rateEmploymentIncrease = BigDecimal(rateConfig.employmentIncrease)
            val rateWelfareIncrease = BigDecimal(rateConfig.welfareIncrease)
            val now = LocalDateTime.now()
            val yearMonth = yearMonth.takeIf {
                it != null
            } ?: run {
                YearMonth.now()
            }
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

                EmployeeSalary(
                    employeeSalaryId = 0,
                    employeeId = e.employeeId,
                    year = yearMonth.year,
                    month = yearMonth.monthValue,
                    salaryOfMonth = e.salaryOfMonth,
                    healthInsurance = healthInsurance.toInt(),
                    welfarePension = welfarePension.toInt(),
                    employmentIncrease = employmentIncrease.toInt(),
                    incomeTax = 0, // TODO
                    salaryPaid = salaryPaid.toInt(),
                    createdBy = loginId,
                    createdAt = now,
                    updatedBy = loginId,
                    updatedAt = now,
                )
            }.toList()

            employeeSalaryMapper.upsert(employeeSalaries)
            // sftp送信
            sendToSftp(employeeSalaries, yearMonth)
        }.fold(
            onSuccess = {
                return RepeatStatus.FINISHED
            },
            onFailure = {
                log.debug("error.", it)
                throw RuntimeException("failed to calcSalaryOfMonth.", it)
            }
        )
    }

    private fun sendToSftp(employeeSalaries: List<EmployeeSalary>, yearMonth: YearMonth) {
        val csv = employeeSalaries.asSequence().map { EmployeeSalaryCSV.of(it) }.toList()

        val tmpDir = System.getProperty("java.io.tmpdir").let {
            val separator = System.getProperty("file.separator")
            if (it.takeLast(1) != separator) {
                it + separator
            } else {
                it
            }
        }
        val fileName = "employeeSalaries_${yearMonth}.csv"
        val tmpFilePath = "${tmpDir}${fileName}"

        runCatching {
            FileWriter(tmpFilePath).use { writer ->
                val beanToCsv = StatefulBeanToCsvBuilder<EmployeeSalaryCSV>(writer).build()
                beanToCsv.write(csv)
            }

            val tmpFile = Paths.get(tmpFilePath).toFile()

            val sftpClient = SftpClientBuilder.build(sftpConfig).apply {
                this.setRemoteDirectoryExpression(LiteralExpression(sftpConfig.subdir))
                // これがないと同名ファイル上書き時にエラーとなる
                this.isUseTemporaryFileName = false
            }

            // ファイル送信
            sftpClient.send(
                MessageBuilder.withPayload(tmpFile).build()
            ).also {
                tmpFile.delete()
            }

        }.onFailure {
            Paths.get(tmpFilePath).toFile().delete()
            throw it
        }
    }
}