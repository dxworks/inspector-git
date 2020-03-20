package org.dxworks.inspectorgit.gitclient.extractors

import org.dxworks.inspectorgit.gitclient.dto.gitlog.HunkDTO

abstract class MetaExtractor<T> {
    abstract val linePrefix: String
    fun write(hunkDTO: HunkDTO): String {
        return "$linePrefix${extract(hunkDTO)}"
    }

    protected abstract fun extract(hunkDTO: HunkDTO): String

    fun read(line: String): T {
        return parse(line.removePrefix(linePrefix))
    }

    protected abstract fun parse(line: String): T
}