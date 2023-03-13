package com.example.demo.usecase

import com.example.demo.exception.ApplicationException
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.stereotype.Service
import java.util.*

interface CalcEmployeeSalaryUseCase {
    fun execute()
}

@Service
class CalcEmployeeSalaryUseCaseImpl(
    private val jobLauncher: JobLauncher,
    private val calcSalaryOfMonth: Job
) : CalcEmployeeSalaryUseCase {
    override fun execute() {
        val params: JobParameters = JobParametersBuilder()
            .addString("name", "calcEmployeeSalary")
            .addString("uuid", UUID.randomUUID().toString())
            .toJobParameters()

        val result = jobLauncher.run(calcSalaryOfMonth, params)

        if (result.status != BatchStatus.COMPLETED) {
            throw ApplicationException("failed to calc salary.")
        }
    }
}