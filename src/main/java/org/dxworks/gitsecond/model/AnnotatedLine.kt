package org.dxworks.gitsecond.model

data class AnnotatedLine(var commit: Commit, var lineNumber: Int, var content: String) {
    override fun equals(other: Any?): Boolean {
        if (other is AnnotatedLine)
            return (commit.id == other.commit.id).and(lineNumber == other.lineNumber).and(content == other.content)
        else
            return false
    }
}