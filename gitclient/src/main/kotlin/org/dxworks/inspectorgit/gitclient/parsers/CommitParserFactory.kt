package org.dxworks.inspectorgit.gitclient.parsers

import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.gitclient.parsers.impl.MergeCommitParser
import org.dxworks.inspectorgit.gitclient.parsers.impl.SimpleCommitParser

class CommitParserFactory {
    companion object {
        fun createAndParse(commitsGroup: List<List<String>>, gitClient: GitClient) =
                if (extractParentIds(commitsGroup.first()).size > 1)
                    MergeCommitParser(commitsGroup, gitClient).parse(emptyList())
                else
                    SimpleCommitParser().parse(commitsGroup[0])

        private fun extractParentIds(lines: List<String>) =
                lines[1].removePrefix("parents: ").split(" ")
    }
}
