package org.dxworks.inspectorgit.gitclient.parsers

interface GitParser<T> {
    fun parse(lines: List<String>): T
}