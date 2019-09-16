package org.dxworks.inspectorgit.parsers

interface GitParser<T> {
    fun parse(lines: MutableList<String>): T
}