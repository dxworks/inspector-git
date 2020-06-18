package org.dxworks.inspectorgit.remote.miners

import org.springframework.http.HttpHeaders
import org.springframework.scheduling.annotation.Async


abstract class PRMiner {
    protected fun createHeaders(username: String, password: String): HttpHeaders {
        val httpHeaders = HttpHeaders()
        httpHeaders.setBasicAuth(username, password)
        return httpHeaders
    }

    @Async
    abstract fun saveToDatabase(options: PRMinerOptions)
}