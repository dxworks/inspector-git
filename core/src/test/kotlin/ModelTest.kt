import com.google.gson.Gson
import org.dxworks.inspectorgit.gitClient.GitClient
import org.dxworks.inspectorgit.gitClient.dto.AnnotatedLineDTO
import org.dxworks.inspectorgit.gitClient.dto.GitLogDTO
import org.dxworks.inspectorgit.gitClient.enums.ChangeType
import org.dxworks.inspectorgit.gitClient.parsers.LogParser
import org.dxworks.inspectorgit.model.AnnotatedLine
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.transformers.ProjectTransformer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.nio.file.Paths

internal class ModelTest {

    companion object {
        private lateinit var gitClient: GitClient
        private lateinit var gitLogDTO: GitLogDTO
        private lateinit var project: Project

        private val LOG = LoggerFactory.getLogger(ModelTest::class.java)
        private val dxPlatformPath = Paths.get("C:\\Users\\nagyd\\Documents\\DX\\dx\\dx-platform")
        private val IGMergeTestPath = Paths.get("C:\\Users\\nagyd\\Documents\\DX\\testRepo\\IGmergeTest")
        private val kafkaPath = Paths.get("C:\\Users\\nagyd\\Documents\\DX\\kafkaRepo\\kafka")
        private val manCxPath = Paths.get("C:\\Users\\nagyd\\Documents\\DD\\man\\mansp-cx")
        private val manUiPath = Paths.get("C:\\Users\\nagyd\\Documents\\DD\\man\\mansp-ui")

        private val tmpFolder = Paths.get(System.getProperty("java.io.tmpdir")).resolve("inspectorGitTest")

        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            val tmpFolderFile = tmpFolder.toFile()
            tmpFolderFile.mkdirs()

            val repoPath = dxPlatformPath

            val repoName = repoPath.fileName.toString()
            val repoCache = tmpFolder.resolve("$repoName.json").toFile()
            gitClient = GitClient(repoPath)
            if (repoCache.exists())
                gitLogDTO = Gson().fromJson(repoCache.readText(), GitLogDTO::class.java)
            else {
                gitLogDTO = LogParser(gitClient).parse(gitClient.getLogs())
                repoCache.createNewFile()
                repoCache.writeText(Gson().toJson(gitLogDTO))
            }
//            project = ProjectTransformer(gitLogDTO, repoName, TestChangeFactory(gitClient)).transform()
            project = ProjectTransformer(gitLogDTO, repoName).transform()
        }
    }


    @Test
    fun `test that all commits exist in the project`() {
        val commitIds = gitClient.runGitCommand("log --format=\"%H\"")
        assertTrue { commitIds!!.all { project.commitRegistry.contains(it) } }
    }

    @Test
    fun `test blames for all files for all commits`() {
        var ok = true
        LOG.debug("Number of commits: ${project.commitRegistry.all.size}")
        LOG.debug("Number of changes: ${project.commitRegistry.all.map { it.changes.size }.sum()}")
        var i = 0
        var j = 0
        project.commitRegistry.all.forEach { commit ->
            LOG.debug("$i) test for commit: ${commit.id}")
            i++
            commit.changes.filter { it.type != ChangeType.DELETE && !it.file.isBinary }
                    .forEach { change ->
                        val fileName = change.file.id
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
        assertTrue { ok }
    }

    private fun blameAndFileContentAreTheSame(blame: List<String>, annotatedLines: List<AnnotatedLine>, fileName: String, commitId: String): Boolean {
        if (blame.size != annotatedLines.size) {
            LOG.error("$fileName blames have a different number of lines")
            return false
        }
        val annotatedLineDTOs = blame.map { parseAnnotatedLine(it) }
        for (i in 1 until annotatedLineDTOs.size) {
            val annotatedLineDTO = annotatedLineDTOs[i]
            val annotatedLine = annotatedLines[i]
            if (!linesAreTheSame(annotatedLineDTO, annotatedLine, fileName, commitId)) {
                LOG.error("$fileName is not correct in $commitId because:\n$annotatedLineDTO differs from $annotatedLine")
                return false
            }
        }
        return true
    }

    private fun linesAreTheSame(annotatedLineDTO: AnnotatedLineDTO, annotatedLine: AnnotatedLine, fileName: String, commitId: String): Boolean {
        val numberAndContentAreTheSame = annotatedLineDTO.number == annotatedLine.number &&
                annotatedLineDTO.content == annotatedLine.content
        if (project.commitRegistry.getById(annotatedLineDTO.commitId) != annotatedLine.commit)
            LOG.warn("In $fileName at $commitId at line ${annotatedLineDTO.number} commits differ blame: ${annotatedLineDTO.commitId}, IG: ${annotatedLine.commit.id}")
        return numberAndContentAreTheSame
    }

    private fun parseAnnotatedLine(it: String): AnnotatedLineDTO {
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