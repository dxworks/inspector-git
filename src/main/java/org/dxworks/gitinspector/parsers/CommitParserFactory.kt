package org.dxworks.gitinspector.parsers

import org.dxworks.gitinspector.GitClient
import org.dxworks.gitinspector.parsers.abstracts.CommitParser
import org.dxworks.gitinspector.parsers.impl.MergeCommitParser
import org.dxworks.gitinspector.parsers.impl.SimpleCommitParser

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
