package com.example.demo.job

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.transaction.PlatformTransactionManager

abstract class AbstractJob(
    private val jobRepository: JobRepository,
    private val txManager: PlatformTransactionManager,
) {
    fun job(): Job {
        return JobBuilder(getJobName(), jobRepository)
            .incrementer(RunIdIncrementer())
            .start(step())
            .build()
    }

    fun step(): Step {
        return StepBuilder(getStepName(), jobRepository)
            .tasklet(getTasklet(), txManager)
            .build()
    }

    abstract fun getJobName(): String

    abstract fun getStepName(): String

    abstract fun getTasklet(): Tasklet
}