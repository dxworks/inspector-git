package org.dxworks.inspectorgit.gitclient.iglog.writers

abstract class IGWriter(protected val incognito: Boolean = false) {
    fun write(): String {
        appendLines(responseBuilder)
        return responseBuilder.toString()
    }

    protected abstract fun appendLines(responseBuilder: StringBuilder)

    private val responseBuilder = StringBuilder()
}
