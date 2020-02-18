package org.dxworks.inspectorgit.model

data class AnnotatedContent(val commit: Commit) {

    companion object {
        private var counter: Int = 0
    }

    init {
        counter++
    }

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        var result = commit.hashCode()
        result += counter
        return result
    }
}