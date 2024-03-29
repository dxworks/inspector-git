package org.dxworks.inspectorgit.gitclient.dto.gitlog

class AnnotatedLineDTO(val commitId: String, val number: Int, val content: String?) {
    override fun toString(): String {
        return "$commitId $number) $content"
    }
}
