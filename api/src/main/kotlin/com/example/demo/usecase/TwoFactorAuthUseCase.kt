package com.example.demo.usecase

import com.example.demo.exception.InvalidRequestException
import com.example.demo.repository.EmployeeMapper
import com.example.demo.repository.OneTimeTokenMapper
import com.example.demo.request.TwoFactorAuthRequest
import com.example.demo.response.TwoFactorAuthResponse
import com.example.demo.utils.JwtUtil
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
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

        val employeeTwoFactorAuth =
            employeeMapper.findByLoginIdValidToken(
                request.loginId,
                request.otpReqId,
                LocalDateTime.now()
            ) ?: throw InvalidRequestException("invalid request.")

        employeeTwoFactorAuth.oneTimeToken.takeIf {
            BCryptPasswordEncoder().matches(request.oneTimeToken, it)
        } ?: throw InvalidRequestException("one time token is incorrect.")

        val token = jwtUtil.create(employeeTwoFactorAuth.tokenId)

        employeeMapper.updateTokenById(
            employeeId = employeeTwoFactorAuth.employeeId,
            token = token,
            updatedBy = employeeTwoFactorAuth.loginId,
            now = LocalDateTime.now(),
        )

        oneTimeTokenMapper.deleteByEmployeeId(employeeTwoFactorAuth.employeeId)

        return TwoFactorAuthResponse(
            token = token
        )
    }
}