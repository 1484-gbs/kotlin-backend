package com.example.demo.controller

import com.example.demo.request.LoginRequest
import com.example.demo.request.TwoFactorAuthRequest
import com.example.demo.response.LoginResponse
import com.example.demo.response.TwoFactorAuthResponse
import com.example.demo.usecase.LoginUseCase
import com.example.demo.usecase.TwoFactorAuthUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginController(
    private val loginUseCase: LoginUseCase,
    private val twoFactorAuthUseCase: TwoFactorAuthUseCase
) : AbstractController() {

    /**
     * ログイン
     */
    @PostMapping("/login")
    @ResponseBody
    fun login(@RequestBody request: LoginRequest): LoginResponse {
        return loginUseCase.execute(request)
    }

    /**
     * 2FA
     */
    @PostMapping("/2fa")
    @ResponseBody
    fun twoFactorAuth(@RequestBody request: TwoFactorAuthRequest): TwoFactorAuthResponse {
        return twoFactorAuthUseCase.execute(request)
    }
}