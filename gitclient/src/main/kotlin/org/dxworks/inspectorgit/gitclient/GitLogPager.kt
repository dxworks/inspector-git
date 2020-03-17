package org.dxworks.inspectorgit.gitclient

import org.slf4j.LoggerFactory

class GitLogPager(private val gitClient: GitClient, var pageSize: Int = 100) {
    companion object {
        private val LOG = LoggerFactory.getLogger(GitLogPager::class.java)
    }

    var commitCount = gitClient.getCommitCount()
        set(value) {
            field = value
            pageCount = value / pageSize + 1
        }
    private var pageCount = commitCount / pageSize + 1
    private var counter = 1

    init {
        gitClient.setRenameLimit()
    }


    private fun page(number: Int): List<String> {
        if (number > pageCount) throw IllegalArgumentException("Page number: $number exceeds page count: $pageCount")
        if (number < 1) throw IllegalArgumentException("Page number must be positive! Received $number")

        val skippedCommits = commitCount - pageSize * number
        val (numberOfCommits, skip) = if (skippedCommits < 0)
            Pair(pageSize + skippedCommits, 0)
        else
            Pair(pageSize, skippedCommits)

        LOG.info("Getting Page $number/$pageCount containing commits $skip-${skip + numberOfCommits} of $commitCount")
        return gitClient.getNCommitLogs(numberOfCommits, skip)
    }

    fun hasNext() = counter <= pageCount

    fun next() = page(counter++)

    fun reset() {
        counter = 1
    }
}