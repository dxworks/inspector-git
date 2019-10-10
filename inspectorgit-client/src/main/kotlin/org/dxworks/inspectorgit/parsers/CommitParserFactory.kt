package org.dxworks.inspectorgit.parsers

import org.dxworks.inspectorgit.GitClient
import org.dxworks.inspectorgit.parsers.abstracts.CommitParser
import org.dxworks.inspectorgit.parsers.impl.MergeCommitParser
import org.dxworks.inspectorgit.parsers.impl.SimpleCommitParser

class CommitParserFactory {
    companion object {
        private const val parents = "parents: "

        fun create(lines: MutableList<String>, gitClient: GitClient): CommitParser {
            return if (getParents(lines).size > 1) MergeCommitParser(gitClient) else SimpleCommitParser()
        }

        private fun getParents(lines: List<String>) =
                lines.find { it.startsWith(parents) }!!.removePrefix(parents).split(" ")
    }
}
