package com.example.demo.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.example.demo.exception.handler.UnAuthorizeException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class JwtUtil {
    companion object {
        private const val EXPIRED_HOUR = 4L
        private val ALGORITHM = Algorithm.HMAC256("gbservice")
        const val Aud = "https://gbservice.co.jp"
        const val Sub = "gbs"
        fun create(tokenId: String): String {
            val nowInstant = getInstant(0L)
            return JWT.create()
                .withClaim(JwtClaimType.TOKEN_ID.value, tokenId)
                .withIssuedAt(nowInstant)
                .withExpiresAt(getInstant(EXPIRED_HOUR))
                .withNotBefore(nowInstant)
                .withSubject(Sub)
                .withAudience(Aud)
                .sign(ALGORITHM)
        }

        fun verify(token: String): DecodedJWT {
            return runCatching {
                val verifier = JWT.require(ALGORITHM).build()
                verifier.verify(token)
            }.fold(
                onSuccess = { it },
                onFailure = { ex ->
                    when (ex) {
                        is JWTVerificationException -> throw UnAuthorizeException()
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
    }

    enum class JwtClaimType(val value: String) {
        TOKEN_ID("token_id")
    }
}