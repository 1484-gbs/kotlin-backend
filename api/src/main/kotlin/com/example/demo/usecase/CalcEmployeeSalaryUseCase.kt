package com.example.demo.usecase

import com.example.demo.dto.UserDetailImpl
import com.example.demo.exception.ApplicationException
import com.example.demo.type.JobNameType
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.stereotype.Service
import java.time.YearMonth
import java.util.*

interface CalcEmployeeSalaryUseCase {
    fun execute(yearMonth: YearMonth?, user: UserDetailImpl.UserDetail)
}

@Service
class CalcEmployeeSalaryUseCaseImpl(
    private val jobLauncher: JobLauncher,
    private val calcSalaryOfMonth: Job,
) : CalcEmployeeSalaryUseCase {
    override fun execute(yearMonth: YearMonth?, user: UserDetailImpl.UserDetail) {
        val params = JobParametersBuilder().apply {
            this.addString("name", JobNameType.CALC_SALARY_OF_MONTH.value)
            this.addString("uuid", UUID.randomUUID().toString())
            this.addString("loginId", user.loginId, false)
            yearMonth?.let { this.addString("targetYearMonth", it.toString(), false) }
        }.toJobParameters()

        val result = jobLauncher.run(calcSalaryOfMonth, params)

        if (result.status != BatchStatus.COMPLETED) {
            throw ApplicationException("failed to calc salary.")
        }
    }
}