package com.example.demo.filter

import com.example.demo.dto.UserDetailImpl
import com.example.demo.exception.handler.UnAuthorizeException
import com.example.demo.type.RoleType
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Component
class RoleFilter : OncePerRequestFilter() {

    companion object {
        private val Deny_URL = mapOf(
            RoleType.ADMIN to listOf<DenyUrl>(),
            RoleType.MANAGER to listOf(
                DenyUrl(HttpMethod.POST.toString(), "/employee/calc_salary"),
                DenyUrl(HttpMethod.DELETE.toString(), "/employee/.*"),
            ),
            RoleType.GENERAL to listOf(
                DenyUrl(HttpMethod.POST.toString(), "/employee/calc_salary"),
                DenyUrl(HttpMethod.POST.toString(), "/employee"),
                DenyUrl(HttpMethod.DELETE.toString(), "/employee/.*"),
            )
        )
    }

    data class DenyUrl(
        val method: String,
        val url: String
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        runCatching {
            SecurityContextHolder.getContext().authentication?.let { sca ->
                val user = sca.principal as UserDetailImpl.UserDetail?
                user?.let { u ->
                    Deny_URL[u.role]?.let { denyUrl ->
                        if (denyUrl.any { deny ->
                                deny.method == request.method
                                        && isDenyUrl(deny.url, request.requestURI)
                            }
                        ) {
                            throw UnAuthorizeException()
                        }
                    } ?: throw UnAuthorizeException() // invalid role.
                } ?: throw UnAuthorizeException()// invalid authentication.
            }
        }.fold(
            onSuccess = {
                filterChain.doFilter(request, response)
            },
            onFailure = { ex ->
                when (ex) {
                    is UnAuthorizeException -> response.status =
                        HttpStatus.UNAUTHORIZED.value()

                    else -> throw RuntimeException(ex)
                }
            }
        )
    }

    private fun isDenyUrl(denyUrl: String, reqUrl: String): Boolean {
        val denyUrlSplit = denyUrl.split("/").filter { u -> u.isNotEmpty() }
        val reqUrlSplit = reqUrl.split("/").filter { u -> u.isNotEmpty() }

        (denyUrlSplit.size != reqUrlSplit.size)
            .takeIf {
                it
            }?.run {
                return false
            }

        denyUrlSplit.indices.forEach { idx ->
            (reqUrlSplit[idx].matches(Regex(denyUrlSplit[idx])))
                .takeUnless {
                    it
                }?.run {
                    return false
                }
        }
        return true
    }
}