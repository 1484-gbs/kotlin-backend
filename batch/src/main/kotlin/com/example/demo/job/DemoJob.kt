package com.example.demo.job

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager


//@EnableBatchProcessing
@Configuration
class DemoJob(
    private val txManager: PlatformTransactionManager,
) {

    @Bean
    fun jobDemo(jobRepository: JobRepository, @Qualifier("stepDemo") step: Step): Job {
        return JobBuilder("demo", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(step)
            .build()
    }

    @Bean
    fun stepDemo(jobRepository: JobRepository, demoTasklet: DemoTasklet): Step {
        return StepBuilder("demoStep", jobRepository).tasklet(demoTasklet, txManager).build()
    }

    @Component
    class DemoTasklet : Tasklet {
        override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
            println("hello.")
            return RepeatStatus.FINISHED
        }
    }
}