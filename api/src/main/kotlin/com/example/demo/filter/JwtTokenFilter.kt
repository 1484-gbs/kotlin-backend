package com.example.demo.filter

import com.example.demo.exception.handler.UnAuthorizeException
import com.example.demo.repository.EmployeeMapper
import com.example.demo.utils.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
class JwtTokenFilter(
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
        if (IGNORE_URL.contains(request.requestURI)) {
            return filterChain.doFilter(request, response)
        }

        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        (header.isNullOrEmpty() || !header.startsWith(BEARER)).takeIf {
            it
        }?.run { throw UnAuthorizeException() }

        val token = header.replace(BEARER, "")
        val jwt = JwtUtil.verify(token)
        val now = Date()
        jwt.takeIf {
            now.before(it.notBefore)
                    || now.after(it.expiresAt)
        }?.run {
            throw UnAuthorizeException()
        }

        val loginId = JwtUtil.getClimeString(jwt, JwtUtil.JwtClaimType.LOGIN_ID)
            ?: throw UnAuthorizeException()

        employeeMapper.findByLoginId(loginId) ?: throw UnAuthorizeException()

        val authentication = UsernamePasswordAuthenticationToken(
            User(loginId, "dummy", listOf()),
            null
        )
        SecurityContextHolder.getContext().authentication = authentication
        filterChain.doFilter(request, response)
    }
}