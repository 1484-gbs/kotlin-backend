package com.example.demo.job

import com.example.demo.type.JobNameType
import com.example.demo.tasklet.CalcSalaryOfMonthTasklet
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class CalcSalaryOfMonthJob(
    val txManager: PlatformTransactionManager,
    val jobRepository: JobRepository,
    private val calcSalaryOfMonthTasklet: CalcSalaryOfMonthTasklet
) : AbstractJob(jobRepository, txManager) {

    @Bean
    fun calcSalaryOfMonth(): Job {
        return super.job()
    }

    @Bean
    fun calcSalaryOfMonthStep(): Step {
        return super.step()
    }

    override fun getJobName(): String {
        return JobNameType.CALC_SALARY_OF_MONTH.value
    }

    override fun getStepName(): String {
        return "calc_salary_of_month"
    }

    override fun getTasklet(): Tasklet {
        return calcSalaryOfMonthTasklet
    }
}