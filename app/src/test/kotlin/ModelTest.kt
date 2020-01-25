import org.dxworks.inspectorgit.gitClient.GitClient
import org.dxworks.inspectorgit.gitClient.dto.GitLogDTO
import org.dxworks.inspectorgit.gitClient.parsers.LogParser
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

        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            gitClient = GitClient(IGMergeTestPath)
            gitLogDTO = LogParser().parse(gitClient.getLogs())
            project = ProjectTransformer(gitLogDTO, "dx-platform").transform()
        }
    }


    @Test
    fun testAllCommits() {
        val commitIds = gitClient.runGitCommand("log --format=\"%H\"")
        assertTrue { commitIds.all { project.commitRegistry.contains(it) } }
    }
}