package org.dxworks.gitinspector.parsers

interface GitParser<T> {
    fun parse(lines: MutableList<String>): T
}