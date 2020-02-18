package org.dxworks.inspectorgit.gitClient.dto

class AnnotatedLineDTO(val commitId: String, val number: Int) {
    override fun toString(): String {
        return "$commitId $number"
    }
}
