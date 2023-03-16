package com.example.demo.filter

import com.auth0.jwt.exceptions.JWTVerificationException
import com.example.demo.dto.UserDetailImpl
import com.example.demo.exception.handler.UnAuthorizeException
import com.example.demo.repository.EmployeeMapper
import com.example.demo.utils.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val employeeMapper: EmployeeMapper
) : OncePerRequestFilter() {

    companion object {
        private const val BEARER = "Bearer "
        private val IGNORE_URL = listOf("/login", "/health")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        runCatching {
            if (IGNORE_URL.contains(request.requestURI)) {
                return@runCatching
            }

            val header = request.getHeader(HttpHeaders.AUTHORIZATION)
            (header.isNullOrEmpty() || !header.startsWith(BEARER))
                .takeIf {
                    it
                }?.run { throw UnAuthorizeException() }

            val token = header.replace(BEARER, "")
            val jwt = JwtUtil.verify(token)

            val tokenId = JwtUtil.getClimeString(jwt, JwtUtil.JwtClaimType.TOKEN_ID)
                .takeIf {
                    !it.isNullOrEmpty()
                } ?: throw UnAuthorizeException()

            val employee = employeeMapper.findByTokenId(tokenId)?.let {
                it.takeIf { e ->
                    e.token == token
                } ?: throw UnAuthorizeException()
            } ?: throw UnAuthorizeException()

            val authentication = UsernamePasswordAuthenticationToken(
                UserDetailImpl.UserDetail(
                    loginId = employee.loginId,
                    token = token
                ),
                null
            )
            SecurityContextHolder.getContext().authentication = authentication
        }.fold(
            onSuccess = {
                filterChain.doFilter(request, response)
            },
            onFailure = { ex ->
                when (ex) {
                    is UnAuthorizeException, is JWTVerificationException -> response.status =
                        HttpStatus.UNAUTHORIZED.value()

                    else -> throw RuntimeException(ex)
                }
            }
        )

    }
}