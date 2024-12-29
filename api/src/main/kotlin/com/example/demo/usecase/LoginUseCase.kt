package com.example.demo.usecase

import com.example.demo.client.DynamoDBClient
import com.example.demo.exception.InvalidRequestException
import com.example.demo.repository.EmployeeMapper
import com.example.demo.request.LoginRequest
import com.example.demo.response.LoginResponse
import com.example.demo.type.dynamodb.OneTimeTokenType
import com.example.demo.type.dynamodb.TokenType
import com.example.demo.usecase.common.SendMail
import com.example.demo.utils.JwtUtil
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

interface LoginUseCase {
    fun execute(request: LoginRequest): LoginResponse
}

@Service
@Transactional
class LoginUseCaseCaseImpl(
    private val employeeMapper: EmployeeMapper,
    private val jwtUtil: JwtUtil,
    private val dynamoDBClient: DynamoDBClient,
    private val sendMail: SendMail,
) : LoginUseCase {

    @Value("\${app.is2fa:true}")
    private var is2fa: Boolean = true

    @Value("\${app.oneTimeToken.expiredAtTime:0}")
    private var expiredAtTime: Long = 0

    @Value("\${app.isDebug:false}")
    private var isDebug: Boolean = false

    @Value("\${app.oneTimeToken.length:6}")
    private var oneTimeTokenLength: Int = 6

    override fun execute(request: LoginRequest): LoginResponse {
        val employee = employeeMapper.findByLoginId(request.loginId)
            ?: throw InvalidRequestException("loginId or password is incorrect.")

        request.password.takeIf {
            BCryptPasswordEncoder().matches(it, employee.password)
        } ?: throw InvalidRequestException("loginId or password is incorrect.")

        if (is2fa) {
            // ワンタイムトークン生成
            //val oneTimeToken = (Random().nextInt(1000000)).toString().padStart(6, '0')
            val oneTimeToken = RandomStringUtils.random(oneTimeTokenLength, true, true)
            val otpReqId = UUID.randomUUID().toString()

            dynamoDBClient.putItem(
                OneTimeTokenType.TABLE_NAME.value,
                mapOf(
                    OneTimeTokenType.EMPLOYEE_ID.value to
                        AttributeValue.builder().n(employee.employeeId.toString()).build(),
                    OneTimeTokenType.ONE_TIME_TOKEN.value to
                        AttributeValue.builder().s(BCryptPasswordEncoder().encode(oneTimeToken)).build(),
                    OneTimeTokenType.OPT_REQ_ID.value to
                        AttributeValue.builder().s(otpReqId).build(),
                    OneTimeTokenType.TTL.value to
                        AttributeValue.builder().n(
                            LocalDateTime.now().plusMinutes(expiredAtTime).atZone(ZoneId.systemDefault())
                                .toEpochSecond().toString()
                        ).build()
                )
            )

            // token削除
            dynamoDBClient.deleteItem(
                TokenType.TABLE_NAME.value,
                mapOf(
                    TokenType.TOKEN_ID.value
                        to AttributeValue.builder().s(employee.tokenId).build()
                )
            )

            // TODO 文章など
            sendMail.send(
                mailAddress = employee.loginId,
                subject = "",
                text = oneTimeToken
            )

            return LoginResponse(
                onetimeToken = if (isDebug) oneTimeToken else null,
                otpReqId = otpReqId,
            )
        } else {
            val token = jwtUtil.create(
                tokenId = employee.tokenId,
                employeeId = employee.employeeId
            )

            return LoginResponse(
                accessToken = token.first,
                refreshToken = token.second
            )
        }
    }
}