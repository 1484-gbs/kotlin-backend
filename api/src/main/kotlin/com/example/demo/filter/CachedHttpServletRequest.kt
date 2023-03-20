package com.example.demo.filter

import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import org.springframework.util.StreamUtils

/**
 * @see https://www.baeldung.com/spring-http-logging
 */
class CachedHttpServletRequest(request: HttpServletRequest?) : HttpServletRequestWrapper(request) {

    private var cachedPayload: ByteArray? = null

    init {
        request.takeIf { it != null }?.let {
            this.cachedPayload = StreamUtils.copyToByteArray(it.inputStream)
        }
    }

    override fun getInputStream(): ServletInputStream {
        return CachedServletInputStream(this.cachedPayload ?: ByteArray(0))
    }
}