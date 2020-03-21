package org.dxworks.inspectorgit.gitclient.dto.iglog

data class ContentMeta(val totalChars: Int, val spaces: Int) {
    operator fun plus(other: ContentMeta) =
            ContentMeta(totalChars + other.totalChars, spaces + other.spaces)

    operator fun minus(other: ContentMeta) =
            ContentMeta(totalChars - other.totalChars, spaces - other.spaces)

    fun isEmpty() = totalChars == 0
}
