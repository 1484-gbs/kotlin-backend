package com.example.demo.controller

import com.example.demo.request.CreateEmployeeRequest
import com.example.demo.request.PatchEmployeeRequest
import com.example.demo.response.CreateEmployeeResponse
import com.example.demo.response.GetEmployeeListResponse
import com.example.demo.response.GetEmployeeResponse
import com.example.demo.type.GenderType
import com.example.demo.usecase.*
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class EmployeeController(
    private val createEmployeeUseCase: CreateEmployeeUseCase,
    private val getEmployeeUseCase: GetEmployeeUseCase,
    private val patchEmployeeUseCase: PatchEmployeeUseCase,
    private val deleteEmployeeUseCase: DeleteEmployeeUseCase,
    private val getEmployeeListUseCase: GetEmployeeListUseCase,
) : AbstractController() {

    @PostMapping("/employee")
    @ResponseBody
    fun create(@RequestBody request: CreateEmployeeRequest): CreateEmployeeResponse {
        return createEmployeeUseCase.execute(request)
    }

    @GetMapping("/employee/{id}")
    @ResponseBody
    fun get(@PathVariable id: Long): GetEmployeeResponse {
        return getEmployeeUseCase.execute(id)
    }

    @PatchMapping("/employee/{id}")
    @ResponseBody
    fun patch(@PathVariable id: Long, @RequestBody request: PatchEmployeeRequest) {
        patchEmployeeUseCase.execute(id, request)
    }

    @DeleteMapping("/employee/{id}")
    @ResponseBody
    fun delete(@PathVariable id: Long) {
        deleteEmployeeUseCase.execute(id)
    }

    @GetMapping("/employee")
    @ResponseBody
    fun getList(
        @RequestParam("name") name: String?,
        @RequestParam("kana") kana: String?,
        @RequestParam("gender") gender: GenderType?,
        @RequestParam("position_id") positionId: Long?,
    ): GetEmployeeListResponse {
        return getEmployeeListUseCase.execute(name, kana, gender, positionId)
    }
}