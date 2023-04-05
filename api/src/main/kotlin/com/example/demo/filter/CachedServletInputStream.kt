package com.example.demo.filter

import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * @see https://www.baeldung.com/spring-http-logging
 */
class CachedServletInputStream(cachedBody: ByteArray) : ServletInputStream() {

    private var cachedInputStream: InputStream? = null

    init {
        this.cachedInputStream = ByteArrayInputStream(cachedBody)
    }

    override fun read(): Int {
        return cachedInputStream?.read() ?: 0
    }

    override fun isFinished(): Boolean {
        return cachedInputStream?.available() == 0
    }

    override fun isReady(): Boolean {
        return true
    }

    override fun setReadListener(listener: ReadListener?) {
        throw UnsupportedOperationException()
    }
}