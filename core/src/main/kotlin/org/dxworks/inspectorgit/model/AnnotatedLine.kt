package org.dxworks.inspectorgit.model

data class AnnotatedLine(var commit: Commit, var number: Int, var content: String) {
    override fun toString(): String {
        return "${commit.id} (${commit.author.id} ${commit.committerDate} $number) $content"
    }
}