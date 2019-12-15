package org.dxworks.inspectorgit.gitClient.parsers

interface GitParser<T> {
    fun parse(lines: List<String>): T
}