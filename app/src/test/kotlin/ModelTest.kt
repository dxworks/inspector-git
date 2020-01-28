import com.google.gson.Gson
import org.dxworks.inspectorgit.gitClient.GitClient
import org.dxworks.inspectorgit.gitClient.dto.AnnotatedLineDTO
import org.dxworks.inspectorgit.gitClient.dto.GitLogDTO
import org.dxworks.inspectorgit.gitClient.enums.ChangeType
import org.dxworks.inspectorgit.gitClient.parsers.LogParser
import org.dxworks.inspectorgit.model.AnnotatedLine
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.transformers.ProjectTransformer
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import kotlin.test.assertTrue

internal class ModelTest {

    companion object {
        private lateinit var gitClient: GitClient
        private lateinit var gitLogDTO: GitLogDTO
        private lateinit var project: Project

        private val LOG = LoggerFactory.getLogger(ModelTest::class.java)
        private val dxPlatformPath = Paths.get("C:\\Users\\nagyd\\Documents\\DX\\dx\\dx-platform")
        private val IGMergeTestPath = Paths.get("C:\\Users\\nagyd\\Documents\\DX\\testRepo\\IGmergeTest")
        private val kafkaPath = Paths.get("C:\\Users\\nagyd\\Documents\\DX\\kafkaRepo\\kafka")

        private val tmpFolder = Paths.get(System.getProperty("java.io.tmpdir")).resolve("inspectorGitTest")

        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            val tmpFolderFile = tmpFolder.toFile()
            tmpFolderFile.mkdirs()

            val repoPath = kafkaPath
            val repoName = repoPath.fileName.toString()
            val repoCache = tmpFolder.resolve("$repoName.json").toFile()
            if (repoCache.exists())
                gitLogDTO = Gson().fromJson(repoCache.readText(), GitLogDTO::class.java)
            else {
                gitClient = GitClient(kafkaPath)
                gitLogDTO = LogParser().parse(gitClient.getLogs())
                repoCache.createNewFile()
                repoCache.writeText(Gson().toJson(gitLogDTO))
            }
            project = ProjectTransformer(gitLogDTO, "dx-platform").transform()
        }
    }


    @Test
    fun `test that all commits exist in the project`() {
        val commitIds = gitClient.runGitCommand("log --format=\"%H\"")
        assertTrue { commitIds!!.all { project.commitRegistry.contains(it) } }
    }

    @Test
    fun `test blames for all files for all commits`() {
        project.commitRegistry.all.forEach { commit ->
            commit.changes.filter { it.type != ChangeType.DELETE && !it.file.isBinary }
                    .forEach { change ->
                        val fileName = change.newFileName
                        val blame = gitClient.blame(commit.id, fileName)
                        if (blame != null)
                            assertTrue { blameAndFileContentAreTheSame(blame, change.annotatedLines, fileName, commit.id) }
                    }
        }
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
                LOG.error("$annotatedLineDTO differs from $annotatedLine")
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
        val other = it.substring(commitDelimiterIndex + 2)
        val contentDelimiterIndex = other.indexOf(")")
        val authorTimeLineNo = other.substring(0, contentDelimiterIndex)
        val content = other.substring(contentDelimiterIndex + 2)
        val lineNumber = authorTimeLineNo.substring(authorTimeLineNo.lastIndexOf(" ") + 1).toInt()
        return AnnotatedLineDTO(commitId, lineNumber, content)
    }
}