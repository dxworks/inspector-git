package org.dxworks.gitsecond.parsers

class CommitParserFactory {
    companion object {
        private val parents = "parents: "

        fun create(lines: MutableList<String>, gitInfoGatherer: GitInfoGatherer): CommitParser {
            return if (getParents(lines).size > 1) MergeCommitParser(lines, gitInfoGatherer) else SimpleCommitParser(ArrayList(lines))
        }

        private fun getParents(lines: List<String>) =
                lines.find { it.startsWith(parents) }!!.removePrefix(parents).split(" ")
    }
}
