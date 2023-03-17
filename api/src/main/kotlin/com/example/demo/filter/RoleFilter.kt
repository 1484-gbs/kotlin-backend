package com.example.demo.filter

import com.example.demo.config.RoleConfig
import com.example.demo.dto.UserDetailImpl
import com.example.demo.exception.handler.UnAuthorizeException
import com.example.demo.type.RoleType
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Component
class RoleFilter(
    private val roleConfig: RoleConfig
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        runCatching {
            SecurityContextHolder.getContext().authentication?.let { sca ->
                val user = sca.principal as UserDetailImpl.UserDetail?
                user?.let { u ->
                    val denyUrl = when (u.role) {
                        RoleType.ADMIN -> roleConfig.denyUrl.admin
                        RoleType.MANAGER -> roleConfig.denyUrl.manager
                        RoleType.GENERAL -> roleConfig.denyUrl.general
                    }

                    if (denyUrl.any { deny ->
                            deny.method == request.method
                                    && isDenyUrl(deny.url, request.requestURI)
                        }
                    ) {
                        log.debug("deny url. ${request.method} ${request.requestURI} loginId:${user.loginId} role:${user.role} ")
                        throw UnAuthorizeException()
                    }

                } ?: run {
                    log.debug("invalid authentication.")
                    throw UnAuthorizeException()
                }
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