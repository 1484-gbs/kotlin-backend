package com.example.demo.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.StandardCharsets

@Component
class RequestResponseLoggingFilter : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(this::class.java)

    companion object {
        private val IGNORE_LOGGING = listOf("password")
    }


    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        //val requestWrapper = ContentCachingRequestWrapper(request)
        val requestWrapper = CachedHttpServletRequest(request)
        val responseWrapper = ContentCachingResponseWrapper(response)
        val query = requestWrapper.queryString.takeUnless {
            it.isNullOrEmpty()
        }?.let {
            "?${it}"
        } ?: ""

        log.info("Request URL: ${requestWrapper.method} ${requestWrapper.requestURI}$query")
        request.getHeaders(HttpHeaders.AUTHORIZATION)?.let {
            it.asSequence().forEach { value ->
                run {
                    log.info("${HttpHeaders.AUTHORIZATION}: $value")
                }
            }
        }

        String(requestWrapper.inputStream.readBytes(), StandardCharsets.UTF_8).takeIf {
            it.isNotBlank()
        }?.let {
            var body: String = it
            for (field in IGNORE_LOGGING) {
                body = removeIgnoreField(body, field)
            }
            log.info("RequestBody: $body")
        }

        runCatching {
            filterChain.doFilter(requestWrapper, responseWrapper)
        }.also {
//            String(requestWrapper.contentAsByteArray).takeIf {
//                it.isNotBlank()
//            }?.let {
//                log.info("RequestBody2: $it")
//            }
            log.debug(
                "Response: ${responseWrapper.status} ${
                    String(responseWrapper.contentAsByteArray).takeIf { it.isNotBlank() } ?: "{}"
                }"
            )
            responseWrapper.copyBodyToResponse()
        }
    }

    private fun removeIgnoreField(body: String, field: String): String {
        val matchesString = "\"$field\":.*"
        val replaceString = "\"$field\": -- not logging --"
        return body.takeIf {
            it.contains(Regex("$matchesString,"))
        }?.replace(Regex("$matchesString,"), "$replaceString,")
            ?: run {
                body.takeIf {
                    it.contains(Regex(matchesString))
                }?.replace(Regex(matchesString), replaceString)
                    ?: run {
                        body
                    }
            }
    }
}
