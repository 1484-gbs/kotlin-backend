package com.example.demo.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.example.demo.client.DynamoDBClient
import com.example.demo.config.JwtConfig
import com.example.demo.exception.handler.UnAuthorizeException
import com.example.demo.type.dynamodb.JwtType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Component
class JwtUtil(
    private val jwtConfig: JwtConfig,
    private val dynamoDBClient: DynamoDBClient,
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val algorithm = Algorithm.HMAC256(
        Base64.getEncoder().encode(jwtConfig.secret.toByteArray())
    )

    fun create(tokenId: String, employeeId: Long): String {
        val nowInstant = getInstant(0L)
        val expiredAt = getInstant(jwtConfig.expiredHour.toLong())
        val jwt = JWT.create()
                .withClaim(JwtClaimType.TOKEN_ID.value, tokenId)
                .withIssuedAt(nowInstant)
                .withExpiresAt(expiredAt)
                .withNotBefore(nowInstant)
                .withSubject(jwtConfig.sub)
                .withAudience(jwtConfig.aud)
                .sign(algorithm)

        dynamoDBClient.putItem(
            JwtType.TABLE_NAME.value,
            mapOf(
                JwtType.TOKEN_ID.value
                    to AttributeValue.builder().s(tokenId).build(),
                JwtType.TOKEN.value
                    to AttributeValue.builder().s(jwt).build(),
                JwtType.EMPLOYEE_ID.value
                    to AttributeValue.builder().n(employeeId.toString()).build(),
                JwtType.TTL.value
                    to AttributeValue.builder().n(expiredAt.epochSecond.toString()).build()
            )
        )

        return jwt
    }

    fun verify(token: String): DecodedJWT {
        return runCatching {
            val verifier = JWT.require(algorithm).build()
            verifier.verify(token)
        }.fold(
            onSuccess = { it },
            onFailure = { ex ->
                when (ex) {
                    is JWTVerificationException -> {
                        log.warn("jwt is invalid.", ex)
                        throw UnAuthorizeException()
                    }

                    else -> throw RuntimeException(ex)
                }
            }
        )
    }

    fun getClimeString(jwt: DecodedJWT, type: JwtClaimType): String? {
        return jwt.getClaim(type.value).asString()
    }

    private fun getInstant(hours: Long): Instant {
        return LocalDateTime.now()
            .plusHours(hours)
            .atZone(ZoneId.systemDefault())
            .toInstant()
    }

    enum class JwtClaimType(val value: String) {
        TOKEN_ID("token_id")
    }
}