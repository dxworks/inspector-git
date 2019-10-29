import org.dxworks.inspectorgit.enums.LineOperation
import org.dxworks.inspectorgit.model.Commit
import org.dxworks.inspectorgit.model.Project

class WorkAnalyzer {
    companion object {
        fun analyze(project: Project) {
            project.commitRegistry.all.map { analyze(it) }
        }

        private fun analyze(commit: Commit) {
            commit.changes.forEach { change ->
                val addChanges = change.lineChanges.filter { it.operation == LineOperation.ADD }
                val removeChanges = change.lineChanges.filter { it.operation == LineOperation.REMOVE }
                val newWork = addChanges.filter { removeChanges.none { rc -> rc.annotatedLine == it.annotatedLine } }
                val otherWork = addChanges.subtract(newWork)

            }
        }
    }
}