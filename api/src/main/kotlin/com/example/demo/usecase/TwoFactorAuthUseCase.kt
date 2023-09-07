package com.example.demo.usecase

import com.example.demo.client.DynamoDBClient
import com.example.demo.exception.InvalidRequestException
import com.example.demo.repository.EmployeeMapper
import com.example.demo.request.TwoFactorAuthRequest
import com.example.demo.response.TwoFactorAuthResponse
import com.example.demo.type.dynamodb.OneTimeTokenType
import com.example.demo.utils.JwtUtil
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

interface TwoFactorAuthUseCase {
    fun execute(request: TwoFactorAuthRequest): TwoFactorAuthResponse
}

@Service
@Transactional
class TwoFactorAuthUseCaseCaseImpl(
    private val employeeMapper: EmployeeMapper,
    private val jwtUtil: JwtUtil,
    private val dynamoDBClient: DynamoDBClient,
) : TwoFactorAuthUseCase {
    override fun execute(request: TwoFactorAuthRequest): TwoFactorAuthResponse {

        val employee =
            employeeMapper.findByLoginId(request.loginId)
                ?: throw InvalidRequestException("invalid request.")

        val oneTimeToken = dynamoDBClient.getItem(
            OneTimeTokenType.TABLE_NAME.value,
            mapOf(
                OneTimeTokenType.EMPLOYEE_ID.value
                    to AttributeValue.builder().n(employee.employeeId.toString()).build()
            )
        ).item().takeIf {
            it.isNotEmpty()
        } ?: throw InvalidRequestException("invalid request.")

        oneTimeToken.takeIf {
            BCryptPasswordEncoder().matches(request.oneTimeToken, it[OneTimeTokenType.ONE_TIME_TOKEN.value]?.s())
                && it[OneTimeTokenType.OPT_REQ_ID.value]?.s().equals(request.otpReqId)
        } ?: throw InvalidRequestException("one time token is incorrect.")

        dynamoDBClient.deleteItem(
            OneTimeTokenType.TABLE_NAME.value,
            mapOf(
                OneTimeTokenType.EMPLOYEE_ID.value
                    to AttributeValue.builder().n(employee.employeeId.toString()).build()
            )
        )

        val token = jwtUtil.create(
            tokenId = employee.tokenId,
            employeeId = employee.employeeId,
        )

        return TwoFactorAuthResponse(
            accessToken = token.first,
            refreshToken = token.second
        )
    }
}