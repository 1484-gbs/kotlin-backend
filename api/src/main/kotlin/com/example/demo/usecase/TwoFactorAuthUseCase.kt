package com.example.demo.usecase

import com.example.demo.exception.InvalidRequestException
import com.example.demo.repository.EmployeeMapper
import com.example.demo.repository.OneTimeTokenMapper
import com.example.demo.request.TwoFactorAuthRequest
import com.example.demo.response.TwoFactorAuthResponse
import com.example.demo.utils.JwtUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

interface TwoFactorAuthUseCase {
    fun execute(request: TwoFactorAuthRequest): TwoFactorAuthResponse
}

@Service
@Transactional
class TwoFactorAuthUseCaseCaseImpl(
    private val employeeMapper: EmployeeMapper,
    private val oneTimeTokenMapper: OneTimeTokenMapper,
    private val jwtUtil: JwtUtil,
) : TwoFactorAuthUseCase {
    override fun execute(request: TwoFactorAuthRequest): TwoFactorAuthResponse {

        val employee =
            employeeMapper.findByLoginIdValidToken(
                request.loginId,
                request.oneTimeToken,
                request.otpReqId,
                LocalDateTime.now()
            ) ?: throw InvalidRequestException("invalid request.")

        val token = jwtUtil.create(employee.tokenId)

        employeeMapper.updateTokenById(
            employeeId = employee.employeeId,
            token = token,
            loginId = employee.loginId,
            now = LocalDateTime.now(),
        )

        oneTimeTokenMapper.deleteByEmployeeId(employee.employeeId)

        return TwoFactorAuthResponse(
            token = token
        )
    }
}