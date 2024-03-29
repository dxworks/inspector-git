import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.gitclient.dto.gitlog.AnnotatedLineDTO
import org.dxworks.inspectorgit.gitclient.extractors.MetadataExtractionManager
import org.dxworks.inspectorgit.gitclient.iglog.readers.IGLogReader
import org.dxworks.inspectorgit.model.git.ChangeType
import org.dxworks.inspectorgit.model.git.Commit
import org.dxworks.inspectorgit.model.git.GitProject
import org.dxworks.inspectorgit.transformers.GitProjectTransformer
import org.dxworks.inspectorgit.utils.tmpFolder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.nio.file.Paths

internal class ModelTestIT {

    companion object {
        private lateinit var gitClient: GitClient
        private lateinit var project: GitProject

        private val LOG = LoggerFactory.getLogger(ModelTestIT::class.java)
        private val dxPlatformPath = Paths.get("/Users/mario/Dxworks/dx-platform")
        private val mavenPath = Paths.get("/Users/mario/projects/case-studies/maven")
        private val kafkaPath = Paths.get("/Users/mario/projects/case-studies/kafka")
        private val vlcPath = Paths.get("/Users/mario/projects/case-studies/vlc")
        private val sparkPath = Paths.get("/Users/mario/projects/case-studies/spark")
        private val springPath = Paths.get("/Users/mario/projects/case-studies/spring-framework")


        private var lines = 0.0
        private var linesWithDifferentCommit = 0.0

        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            val tmpFolderFile = tmpFolder.toFile()
            tmpFolderFile.mkdirs()

            val repoPath = sparkPath

            val repoName = repoPath.fileName.toString()
            val repoCache = tmpFolder.resolve("$repoName.iglog").toFile()
            gitClient = GitClient(repoPath)
            if (!repoCache.exists()) {
                MetadataExtractionManager(repoPath, tmpFolder).extract()
            }
            val gitLogDTO = IGLogReader().read(repoCache.inputStream())
//            project = ProjectTransformer(gitLogDTO, repoName, org.dxworks.inspectorgit.transformers.git.TestChangeFactory(gitClient)).transform()
            project = GitProjectTransformer(gitLogDTO, repoName).transform()
            println("done")
        }
    }


    @Test
    fun `test that all commits exist in the project`() {
        val commitIds = gitClient.runGitCommand("log --format=\"%H\"")

        val missingCommits = commitIds!!.filterNot { project.commitRegistry.contains(it) }

        LOG.error("Missing commit ids: $missingCommits")

        assertTrue(missingCommits.isEmpty())
    }

    @Test
    fun `test blames for all files for all commits`() {
        var ok = true
        LOG.debug("Number of commits: ${project.commitRegistry.all.size}")
        LOG.debug("Number of changes: ${project.commitRegistry.all.map { it.changes.size }.sum()}")
        var i = 0
        var j = 0
        project.commitRegistry.all.parallelStream().forEach { commit ->
            LOG.debug("$i) test for commit: ${commit.id}")
            i++
            commit.changes.parallelStream().filter { it.type != ChangeType.DELETE && !it.file.isBinary }
                    .forEach { change ->
                        val fileName = change.newFileName
                        LOG.debug("$j) test change for $fileName in ${commit.id}")
                        j++
                        val blame = gitClient.blame(commit.id, fileName)
                        if (blame != null) {
                            if (!blameAndFileContentAreTheSame(blame, change.annotatedLines, fileName, commit.id))
                                ok = false
                        } else
                            LOG.warn("Blame is null for $fileName in ${commit.id}")
                    }
        }

        LOG.debug("Test ended with ${(lines - linesWithDifferentCommit) / lines * 100}% line owner accuracy(against git).")
        LOG.debug("$linesWithDifferentCommit / $lines")
        assertTrue { ok }
    }

    private fun blameAndFileContentAreTheSame(blame: List<String>, annotatedLines: List<Commit>, fileName: String, commitId: String): Boolean {
        if (blame.size != annotatedLines.size) {
            LOG.error("$fileName blames have a different number of lines")
            return false
        }
        val annotatedLineDTOs = blame.map { parseBlameLine(it) }
        for (i in 1 until annotatedLineDTOs.size) {
            val annotatedLineDTO = annotatedLineDTOs[i]
            val annotatedLine = annotatedLines[i]
            checkLinesAreTheSame(annotatedLineDTO, annotatedLine, fileName, commitId)
        }
        return true
    }

    private fun checkLinesAreTheSame(annotatedLineDTO: AnnotatedLineDTO, commit: Commit, fileName: String, commitId: String) {
        lines++
        if (annotatedLineDTO.commitId != commit.id) {
            LOG.warn("In $fileName at $commitId at line ${annotatedLineDTO.number} commits differ blame: ${annotatedLineDTO.commitId}, IG: ${commit.id}")
            linesWithDifferentCommit++
        }
    }

    private fun parseBlameLine(it: String): AnnotatedLineDTO {
        val commitDelimiterIndex = it.indexOf(" ")
        val commitId = it.substring(0, commitDelimiterIndex)
        val other = it.substring(commitDelimiterIndex + 1)
        val contentDelimiterIndex = getContentDelimiterIndex(other)
        val authorTimeLineNo = other.substring(0, contentDelimiterIndex)
        val content = other.substring(contentDelimiterIndex + 2)
        val lineNumber = authorTimeLineNo.substring(authorTimeLineNo.lastIndexOf(" ") + 1).toInt()
        return AnnotatedLineDTO(commitId, lineNumber, content)
    }

    @Test
    fun `test delimiter index`() {
        val other = "clients/src/main/java/org/apache/kafka/common/network/Selector.java (Zhanxiang (Patrick) Huang 2019-05-01 12:40:49 -0700  365)                 close(id);"
        val other1 = "clients/src/main/java/org/apache/kafka/common/config/ConfigDef.java (Matthias J. Sax       2017-02-28 12:35:04 -0800    2)  * Licensed to the Apache Software Foundation (ASF) under one or more"


        assertEquals(125, getContentDelimiterIndex(other))
        assertEquals(121, getContentDelimiterIndex(other1))
    }

    private fun getContentDelimiterIndex(other: String): Int {
        var counter = 1
        val startIndex = other.indexOf("(") + 1
        val tail = other.substring(startIndex)
        for (i in tail.indices) {
            if (tail[i] == '(')
                counter++
            if (tail[i] == ')')
                counter--
            if (counter == 0)
                return startIndex + i
        }
        return other.indexOf(")")
    }
}
