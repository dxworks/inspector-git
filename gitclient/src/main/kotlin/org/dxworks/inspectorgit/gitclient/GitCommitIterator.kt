package org.dxworks.inspectorgit.gitclient

import org.dxworks.inspectorgit.gitclient.iglog.IGLogConstants
import org.dxworks.inspectorgit.utils.tmpFolder
import org.slf4j.LoggerFactory
import java.io.InputStreamReader
import java.io.Reader
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

class GitCommitIterator(gitClient: GitClient, pageSize: Int = 2000, private val pageNumber: Int = 0) {
    companion object {
        private val LOG = LoggerFactory.getLogger(GitCommitIterator::class.java)
    }


    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    private var cachingInProgress = false
    private var index: Int = 0
    private var currentPage: Int = 0
    private var cachingIndex: Int = 0

    private val gitLogPager = GitLogPager(gitClient, pageSize)
    private val tempDir = tmpFolder.resolve("commits").toFile()

    init {
        tempDir.mkdirs()
        tempDir.listFiles()?.let { it.forEach { file -> file.delete() } }
    }

    fun hasNext(): Boolean {
        val files = tempDir.list()
        return gitLogPager.hasNext() || (files != null && files.isNotEmpty()) || cachingInProgress
    }

    fun next(): List<String> {
        index++
        val files = tempDir.list()
        if (!cachingInProgress && (files == null || files.isEmpty())) index = 1

        if (index == 1) {
            thread { cacheNextPage() }
            currentPage++
        }

        LOG.debug("Requesting commit: $index on page: $currentPage")

        return lock.withLock {
            if (index >= cachingIndex || currentPage != gitLogPager.counter) {
                LOG.debug("Waiting for commit: $index of page: $currentPage to be cached")
                condition.await()
            }

            LOG.debug("Reading commit: $index of page: $currentPage")
            val file = tempDir.resolve(index.toString())
            val readLines = file.readLines()
            file.delete()
            readLines
        }
    }

    private fun cacheNextPage() {
        cachingInProgress = true
        val pageInputStream = if (pageNumber == 0) {
            gitLogPager.next()
        } else {
            gitLogPager.page(pageNumber)
        }

        cachingIndex = 0
        var currentCommitLines: MutableList<String> = ArrayList()
        val reader = pageInputStream.reader()
        forEachLine(reader) {
            if (it.startsWith(IGLogConstants.commitIdPrefix)) {
                cacheCommit(currentCommitLines)
                currentCommitLines = ArrayList()
            }
            currentCommitLines.add(it)
        }
        cacheCommit(currentCommitLines)
        cachingInProgress = false
    }

    private fun cacheCommit(commitLines: MutableList<String>) {
        lock.withLock {
            if (cachingIndex != 0) {
                LOG.debug("Caching commit: $cachingIndex of page: ${gitLogPager.counter}")

                tempDir.resolve(cachingIndex.toString()).writeText(commitLines.joinToString("\n"))

                if (index <= cachingIndex) condition.signal()
            }
            cachingIndex++
        }
    }

    private fun forEachLine(reader: InputStreamReader, function: (String) -> Unit) {
        do {
            val readLine = readLine(reader)
            readLine?.let { function(it) }
        } while (readLine != null)
    }

    private fun readLine(reader: Reader): String? {
        var read = 0
        val sb = StringBuilder("")
        while (read >= 0) {
            read = reader.read()
            val c = read.toChar()
            if (c == '\r')
                continue
            else if (c == '\n')
                return sb.toString()
            sb.append(c)
        }
        return null
    }
}
