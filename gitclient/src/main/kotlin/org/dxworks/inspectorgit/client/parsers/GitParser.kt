package org.dxworks.inspectorgit.client.parsers

interface GitParser<T> {
    fun parse(lines: List<String>): T
}