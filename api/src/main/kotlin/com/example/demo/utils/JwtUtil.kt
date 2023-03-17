package com.example.demo.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.example.demo.config.JwtConfig
import com.example.demo.exception.handler.UnAuthorizeException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Component
class JwtUtil(
    private val jwtConfig: JwtConfig
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val algorithm = Algorithm.HMAC256(jwtConfig.secret)

    fun create(tokenId: String): String {
        val nowInstant = getInstant(0L)
        return JWT.create()
            .withClaim(JwtClaimType.TOKEN_ID.value, tokenId)
            .withIssuedAt(nowInstant)
            .withExpiresAt(getInstant(jwtConfig.expiredHour.toLong()))
            .withNotBefore(nowInstant)
            .withSubject(jwtConfig.sub)
            .withAudience(jwtConfig.aud)
            .sign(algorithm)
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