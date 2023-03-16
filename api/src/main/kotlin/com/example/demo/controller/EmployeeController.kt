package com.example.demo.controller

import com.example.demo.dto.UserDetailImpl
import com.example.demo.request.CreateEmployeeRequest
import com.example.demo.request.PatchEmployeePasswordRequest
import com.example.demo.request.PatchEmployeeRequest
import com.example.demo.response.CreateEmployeeResponse
import com.example.demo.response.GetEmployeeListResponse
import com.example.demo.response.GetEmployeeRandomResponse
import com.example.demo.response.GetEmployeeResponse
import com.example.demo.type.GenderType
import com.example.demo.usecase.*
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
class EmployeeController(
    private val createEmployeeUseCase: CreateEmployeeUseCase,
    private val getEmployeeUseCase: GetEmployeeUseCase,
    private val patchEmployeeUseCase: PatchEmployeeUseCase,
    private val deleteEmployeeUseCase: DeleteEmployeeUseCase,
    private val getEmployeeListUseCase: GetEmployeeListUseCase,
    private val calcEmployeeSalaryUseCase: CalcEmployeeSalaryUseCase,
    private val getEmployeeListCSVUseCase: GetEmployeeListCSVUseCase,
    private val patchEmployeePasswordUseCase: PatchEmployeePasswordUseCase,
    private val getEmployeeRandomUseCase: GetEmployeeRandomUseCase,
) : AbstractController() {

    /**
     * 社員情報登録
     */
    @PostMapping("/employee")
    @ResponseBody
    fun create(
        @RequestBody @Validated request: CreateEmployeeRequest,
        @AuthenticationPrincipal user: UserDetailImpl.UserDetail
    ): CreateEmployeeResponse {
        return createEmployeeUseCase.execute(request, user.loginId)
    }

    /**
     * 社員情報取得
     */
    @GetMapping("/employee/{id}")
    @ResponseBody
    fun get(@PathVariable id: Long): GetEmployeeResponse {
        return getEmployeeUseCase.execute(id)
    }

    /**
     * 社員情報更新
     */
    @PatchMapping("/employee/{id}")
    @ResponseBody
    fun patch(
        @PathVariable id: Long,
        @RequestBody @Validated request: PatchEmployeeRequest,
        @AuthenticationPrincipal user: UserDetailImpl.UserDetail
    ) {
        patchEmployeeUseCase.execute(id, request, user.loginId)
    }

    /**
     * 社員パスワード更新
     */
    @PatchMapping("/employee/password")
    @ResponseBody
    fun patchPassword(
        @RequestBody @Validated request: PatchEmployeePasswordRequest,
        @AuthenticationPrincipal user: UserDetailImpl.UserDetail
    ) {
        patchEmployeePasswordUseCase.execute(request, user)
    }

    /**
     * 社員情報削除
     */
    @DeleteMapping("/employee/{id}")
    @ResponseBody
    fun delete(@PathVariable id: Long) {
        deleteEmployeeUseCase.execute(id)
    }

    /**
     * 社員情報一覧取得
     */
    @GetMapping("/employee")
    @ResponseBody
    fun getList(
        @RequestParam("name") name: String?,
        @RequestParam("kana") kana: String?,
        @RequestParam("gender") gender: GenderType?,
        @RequestParam("position_id") positionId: Long?
    ): GetEmployeeListResponse {
        return getEmployeeListUseCase.execute(name, kana, gender, positionId)
    }

    /**
     * 月給計算
     */
    @PostMapping("/employee/calc_salary")
    @ResponseBody
    fun calcEmployeeSalary() {
        calcEmployeeSalaryUseCase.execute()
    }

    /**
     * 社員情報一覧csvダウンロード
     */
    @GetMapping("/employee/csv")
    fun csvDownload(
        @RequestParam("name") name: String?,
        @RequestParam("kana") kana: String?,
        @RequestParam("gender") gender: GenderType?,
        @RequestParam("position_id") positionId: Long?,
    ): ResponseEntity<ByteArray> {
        val headers = HttpHeaders()
        headers.contentDisposition = ContentDisposition.builder("inline")
            .filename("employee_list_${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}.csv")
            .build()
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .headers(headers)
            .body(getEmployeeListCSVUseCase.execute(name, kana, gender, positionId))
    }

    /**
     * らんだむくん　カモン
     */
    @GetMapping("/employee/random")
    @ResponseBody
    fun commonMrRandom(@RequestParam("count") count: Int): GetEmployeeRandomResponse {
        return getEmployeeRandomUseCase.execute(count)
    }
}