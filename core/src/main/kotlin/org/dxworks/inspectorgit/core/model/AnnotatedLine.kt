package org.dxworks.inspectorgit.core.model

data class AnnotatedLine(
        var number: Int,
        var commit: Commit
) {
    override fun toString(): String {
        return "${commit.id} (${commit.author.id} ${commit.committerDate} $number"
    }
}
