package com.example.demo.filter

import com.auth0.jwt.exceptions.JWTVerificationException
import com.example.demo.client.DynamoDBClient
import com.example.demo.dto.UserDetailImpl
import com.example.demo.exception.ApplicationException
import com.example.demo.exception.handler.UnAuthorizeException
import com.example.demo.repository.EmployeeMapper
import com.example.demo.type.dynamodb.JwtType
import com.example.demo.utils.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@Component
class JwtFilter(
    private val employeeMapper: EmployeeMapper,
    private val jwtUtil: JwtUtil,
    private val dynamoDBClient: DynamoDBClient,
) : OncePerRequestFilter() {

    companion object {
        private const val BEARER = "Bearer "
        private val IGNORE_URL = listOf("/login", "/health", "/2fa")
    }

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        runCatching {
            if (IGNORE_URL.contains(request.requestURI)) {
                return@runCatching
            }

            val token = request.getHeader(HttpHeaders.AUTHORIZATION)
                ?.takeIf {
                    it.isNotEmpty() && it.startsWith(BEARER)
                }?.replace(BEARER, "")
                ?: run {
                    log.warn("invalid authorization.")
                    throw UnAuthorizeException()
                }

            val jwt = jwtUtil.verify(token)

            val tokenId = jwtUtil.getClimeString(jwt, JwtUtil.JwtClaimType.TOKEN_ID)
                .takeIf {
                    !it.isNullOrEmpty()
                } ?: run {
                log.warn("token_id is null or empty.")
                throw UnAuthorizeException()
            }

            log.info("tokenId: $tokenId")

            val dynamoJwt = dynamoDBClient.getItem(
                JwtType.TABLE_NAME.value,
                mapOf(
                    JwtType.TOKEN_ID.value
                        to AttributeValue.builder().s(tokenId).build()
                )
            ).item().takeIf {
                it.isNotEmpty() && it[JwtType.TOKEN.value]?.s() == token
            } ?: run {
                log.warn("invalid token.")
                throw UnAuthorizeException()
            }

            dynamoJwt[JwtType.EMPLOYEE_ID.value]?.n()?.let {
                val employee = employeeMapper.findByIdJoinRole(it.toLong())
                    ?: run {
                        log.warn("employee not found. employee id: $it")
                        throw UnAuthorizeException()
                    }
                val authentication = UsernamePasswordAuthenticationToken(
                    UserDetailImpl.UserDetail(
                        loginId = employee.loginId,
                        // TODO 必要？
                        token = token,
                        role = employee.role
                    ),
                    null
                )
                SecurityContextHolder.getContext().authentication = authentication
            } ?: throw ApplicationException("employee id not exists.")

        }.fold(
            onSuccess =
            {
                filterChain.doFilter(request, response)
            },
            onFailure =
            { ex ->
                when (ex) {
                    is UnAuthorizeException,
                    is JWTVerificationException -> response.status =
                        HttpStatus.UNAUTHORIZED.value()

                    else -> {
                        log.error("unexpected error", ex)
                        response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
                    }
                }
            }
        )

    }
}