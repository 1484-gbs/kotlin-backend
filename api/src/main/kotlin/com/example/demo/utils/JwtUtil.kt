package com.example.demo.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class JwtUtil {
    companion object {
        private const val EXPIRED_HOUR = 4L
        private val ALGORITHM = Algorithm.HMAC256("gbservice")
        fun create(loginId: String): String {
            val nowInstance = getInstant(0L)
            return JWT.create()
                .withClaim(JwtClaimType.LOGIN_ID.value, loginId)
                .withIssuedAt(nowInstance)
                .withExpiresAt(getInstant(EXPIRED_HOUR))
                .withNotBefore(nowInstance)
                .sign(ALGORITHM)
        }

        fun verify(token: String): DecodedJWT {
            val verifier = JWT.require(ALGORITHM).build()
            return verifier.verify(token)
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
        LOGIN_ID("login_id")
    }
}