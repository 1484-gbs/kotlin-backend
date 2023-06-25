package com.example.demo.usecase

import com.example.demo.entity.OneTimeToken
import com.example.demo.exception.InvalidRequestException
import com.example.demo.repository.EmployeeMapper
import com.example.demo.repository.OneTimeTokenMapper
import com.example.demo.request.LoginRequest
import com.example.demo.response.LoginResponse
import com.example.demo.utils.JwtUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

interface LoginUseCase {
    fun execute(request: LoginRequest): LoginResponse
}

@Service
@Transactional
class LoginUseCaseCaseImpl(
    private val employeeMapper: EmployeeMapper,
    private val oneTimeTokenMapper: OneTimeTokenMapper,
    private val jwtUtil: JwtUtil,
) : LoginUseCase {

    @Value("\${app.is2fa}")
    private var is2fa: Boolean = true

    @Value("\${app.oneTimeToken.expiredAtTime}")
    private var expiredAtTime: Long = 0

    @Value("\${app.isDebug}")
    private var isDebug: Boolean = false

    override fun execute(request: LoginRequest): LoginResponse {
        val employee = employeeMapper.findByLoginId(request.loginId)
            ?: throw InvalidRequestException("loginId or password is incorrect.")

        request.password.takeIf {
            BCryptPasswordEncoder().matches(it, employee.password)
        } ?: throw InvalidRequestException("loginId or password is incorrect.")

        if (is2fa) {
            // ワンタイムトークン生成
            val oneTimeToken = (Random().nextInt(1000000)).toString().padStart(6, '0')
            val otpReqId = UUID.randomUUID().toString()
            val entity = OneTimeToken(
                employeeId = employee.employeeId,
                oneTimeToken = BCryptPasswordEncoder().encode(oneTimeToken),
                otpReqId = otpReqId,
                expiredAt = LocalDateTime.now().plusMinutes(expiredAtTime)
            )

            oneTimeTokenMapper.create(entity)

            // TODO メール送信

            return LoginResponse(
                token = if (isDebug) oneTimeToken else null,
                otpReqId = otpReqId,
            )
        } else {
            val token = jwtUtil.create(employee.tokenId)

            employeeMapper.updateTokenById(
                employeeId = employee.employeeId,
                token = token,
                updatedBy = employee.loginId,
                now = LocalDateTime.now(),
            )

            return LoginResponse(
                token = token
            )
        }
    }
}