package com.example.demo.controller

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
class HealthController(
    private val jobLauncher: JobLauncher,
    private val jobDemo: Job
) : AbstractController() {

    @GetMapping("/health")
    @ResponseBody
    fun health(): String {

        val adminPass = BCryptPasswordEncoder().encode("hoge")

        val params: JobParameters = JobParametersBuilder()
            .addString("name", "demo")
            .addString("uuid", UUID.randomUUID().toString())
            .toJobParameters()

        jobLauncher.run(jobDemo, params)
        return "ok"
    }
}