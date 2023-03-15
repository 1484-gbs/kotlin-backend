package com.example.demo.usecase

import com.example.demo.exception.InvalidRequestException
import com.example.demo.repository.EmployeeMapper
import com.example.demo.request.LoginRequest
import com.example.demo.response.LoginResponse
import com.example.demo.utils.JwtUtil
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface LoginUseCase {
    fun execute(request: LoginRequest): LoginResponse
}

@Service
@Transactional(readOnly = true)
class LoginUseCaseCaseImpl(
    private val employeeMapper: EmployeeMapper
) : LoginUseCase {
    override fun execute(request: LoginRequest): LoginResponse {
        val employee = employeeMapper.findByLoginId(request.loginId)
            ?: throw InvalidRequestException("loginId or password is incorrect.")

        BCryptPasswordEncoder().matches(request.password, employee.password).takeIf {
            it
        } ?: throw InvalidRequestException("loginId or password is incorrect.")

        return LoginResponse(
            token = JwtUtil.create(employee.loginId)
        )
    }
}