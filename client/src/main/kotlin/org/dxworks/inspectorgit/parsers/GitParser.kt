package org.dxworks.inspectorgit.parsers

interface GitParser<T> {
    fun parse(lines: List<String>): T
}