import org.dxworks.gitsecond.GitClient
import org.dxworks.gitsecond.REPO_NAME
import org.dxworks.gitsecond.model.Project
import org.dxworks.gitsecond.transformers.createProject
import org.eclipse.jgit.api.errors.GitAPIException
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.fail

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectTest {
    private val gitClient = GitClient()
    private lateinit var project: Project

    @BeforeAll
    fun `initialize repository and create project`() {
        try {
            gitClient.cloneAndInitializeRepository("https://github.com/nagyDarius/gitLogTest.git", REPO_NAME)

        } catch (e: GitAPIException) {
            e.printStackTrace()
            fail()
        }

        val commitDatas = gitClient.generateGitLogForDx(REPO_NAME).reversed()
        project = createProject(commitDatas, REPO_NAME)
    }

    @Test
    fun `all files at all commits should be correctly built`() {
        project.fileRegistry.all.forEach { file ->
            file.changes.map { change -> change.commit }.forEach { commit ->
                val fileContent = gitClient.getFileContentForRepoAndRevision(file.fullyQualifiedName, REPO_NAME, commit.id)
                val actual = file.contentForRevision(commit).trim()
                println("Commit: ${commit.isMergeCommit} ${commit.id}      expected for ${file.fullyQualifiedName}")
                println(fileContent.trim())
                println()
                println("Commit: ${commit.id}      actual for ${file.fullyQualifiedName}")
                println(actual)
                print("\n\n\n\n\n")
                assertEquals(fileContent.trim(), actual)
            }
        }

    }

    @Test
    private fun `a random file from a random commit should be correctly built`() {
        val file = project.fileRegistry.all.random()
        val commit = file.changes.random().commit
        val expectedContent = gitClient.getFileContentForRepoAndRevision(file.fullyQualifiedName, REPO_NAME, commit.id).trim()
        val myContent = file.contentForRevision(commit).trim()

        assertEquals(expectedContent, myContent)
    }

}
