package org.dxworks.inspectorgit.model.git

data class AnnotatedLine(var number: Int, var content: AnnotatedContent) {
    override fun toString(): String {
        return "${content.commit.id} (${content.commit.author.id} ${content.commit.committerDate} $number) ${content.content}"
    }
}
