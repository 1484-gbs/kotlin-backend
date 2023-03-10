package com.example.demo.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController: AbstractController() {

    @GetMapping("/health")
    @ResponseBody
    fun health(): String {
        return "ok"
    }
}