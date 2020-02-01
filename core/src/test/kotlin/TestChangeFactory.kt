import org.dxworks.inspectorgit.ChangeFactory
import org.dxworks.inspectorgit.gitClient.GitClient
import org.dxworks.inspectorgit.gitClient.enums.ChangeType
import org.dxworks.inspectorgit.model.Change
import org.dxworks.inspectorgit.model.Commit
import org.dxworks.inspectorgit.model.File
import org.dxworks.inspectorgit.model.LineChange

class TestChangeFactory(private val gitClient: GitClient) : ChangeFactory() {
    override fun create(commit: Commit,
                        type: ChangeType,
                        file: File,
                        parentCommits: List<Commit>,
                        oldFileName: String,
                        newFileName: String,
                        lineChanges: MutableList<LineChange>,
                        parentCommit: Commit?): Change =
            TestChange(commit = commit,
                    type = type,
                    file = file,
                    parentCommits = parentCommits,
                    oldFileName = oldFileName,
                    newFileName = newFileName,
                    lineChanges = lineChanges,
                    parentCommit = parentCommit,
                    gitClient = gitClient)
}