package com.example.demo.usecase

import com.auth0.jwt.JWT
import com.example.demo.client.DynamoDBClient
import com.example.demo.exception.InvalidRequestException
import com.example.demo.request.RefreshTokenRequest
import com.example.demo.response.RefreshTokenResponse
import com.example.demo.type.dynamodb.TokenType
import com.example.demo.utils.JwtUtil
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

interface RefreshTokenUseCase {
    fun execute(request: RefreshTokenRequest): RefreshTokenResponse
}

@Service
@Transactional
class RefreshTokenUseCaseImpl(
    private val jwtUtil: JwtUtil,
    private val dynamoDBClient: DynamoDBClient,
) : RefreshTokenUseCase {
    override fun execute(request: RefreshTokenRequest): RefreshTokenResponse {
        val tokenId = JWT.decode(request.accessToken).claims[JwtUtil.JwtClaimType.TOKEN_ID.value]?.asString()
            ?: throw InvalidRequestException("access token is invalid.")

        dynamoDBClient.getItem(
            TokenType.TABLE_NAME.value,
            mapOf(
                TokenType.TOKEN_ID.value
                    to AttributeValue.builder().s(tokenId).build()
            )
        ).item().takeIf {
            it.isNotEmpty()
                && it[TokenType.ACCESS_TOKEN.value]?.s() == request.accessToken
                && BCryptPasswordEncoder().matches(request.refreshToken, it[TokenType.REFRESH_TOKEN.value]?.s())
        } ?: run {
            throw InvalidRequestException("access token or refresh token is invalid.")
        }

        return RefreshTokenResponse(
            accessToken = jwtUtil.updateAccessToken(tokenId),
            refreshToken = request.refreshToken
        )
    }
}