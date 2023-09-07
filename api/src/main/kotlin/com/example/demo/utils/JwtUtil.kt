package com.example.demo.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import com.example.demo.client.DynamoDBClient
import com.example.demo.config.JwtConfig
import com.example.demo.exception.handler.UnAuthorizeException
import com.example.demo.type.dynamodb.TokenType
import org.apache.commons.lang3.RandomStringUtils
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate
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

    fun create(tokenId: String, employeeId: Long): Pair<String, String> {
        val accessToken = createAccessToken(tokenId)
        val refreshTokenExpiredAt = getInstant(jwtConfig.refreshToken.expiredMinutes)
        val refreshToken = RandomStringUtils.random(jwtConfig.refreshToken.length, true, true)

        dynamoDBClient.putItem(
            TokenType.TABLE_NAME.value,
            mapOf(
                TokenType.TOKEN_ID.value
                    to AttributeValue.builder().s(tokenId).build(),
                TokenType.ACCESS_TOKEN.value
                    to AttributeValue.builder().s(accessToken).build(),
                TokenType.EMPLOYEE_ID.value
                    to AttributeValue.builder().n(employeeId.toString()).build(),
                TokenType.REFRESH_TOKEN.value
                    to AttributeValue.builder().s(BCryptPasswordEncoder().encode(refreshToken)).build(),
                TokenType.TTL.value
                    to AttributeValue.builder().n(refreshTokenExpiredAt.epochSecond.toString()).build()
            )
        )

        return Pair(accessToken, refreshToken)
    }

    fun updateAccessToken(tokenId: String): String {
        val accessToken = createAccessToken(tokenId)
        // update access token
        dynamoDBClient.updateItem(
            tableName = TokenType.TABLE_NAME.value,
            key = mapOf(
                TokenType.TOKEN_ID.value
                    to AttributeValue.builder().s(tokenId).build()
            ),
            item = mapOf(
                TokenType.ACCESS_TOKEN.value
                    to AttributeValueUpdate.builder().value(
                    AttributeValue.builder().s(accessToken).build()
                ).build()
            )
        )
        return accessToken
    }


    fun verify(token: String): DecodedJWT {
        return runCatching {
            val verifier = JWT.require(algorithm).build()
            verifier.verify(token)
        }.fold(
            onSuccess = { it },
            onFailure = { ex ->
                when (ex) {
                    is TokenExpiredException -> {
                        throw UnAuthorizeException("token expired.")
                    }

                    is JWTVerificationException -> {
                        log.warn("access token is invalid.", ex)
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

    private fun getInstant(minutes: Long): Instant {
        return LocalDateTime.now()
            .plusMinutes(minutes)
            .atZone(ZoneId.systemDefault())
            .toInstant()
    }

    private fun createAccessToken(tokenId: String): String {
        val nowInstant = getInstant(0L)
        val accessTokenExpiredAt = getInstant(jwtConfig.accessToken.expiredMinutes)
        return JWT.create()
            .withClaim(JwtClaimType.TOKEN_ID.value, tokenId)
            .withIssuedAt(nowInstant)
            .withExpiresAt(accessTokenExpiredAt)
            .withNotBefore(nowInstant)
            .withSubject(jwtConfig.sub)
            .withAudience(jwtConfig.aud)
            .sign(algorithm)
    }

    enum class JwtClaimType(val value: String) {
        TOKEN_ID("token_id")
    }
}